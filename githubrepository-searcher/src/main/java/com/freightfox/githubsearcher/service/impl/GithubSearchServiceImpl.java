package com.freightfox.githubsearcher.service.impl;

import com.freightfox.githubsearcher.client.GithubClient;
import com.freightfox.githubsearcher.dto.GithubSearchResponse;
import com.freightfox.githubsearcher.dto.RepositoryResponse;
import com.freightfox.githubsearcher.dto.SearchRequestDto;
import com.freightfox.githubsearcher.dto.SearchResponse;
import com.freightfox.githubsearcher.entity.Repository;
import com.freightfox.githubsearcher.exception.GithubApiException;
import com.freightfox.githubsearcher.repository.RepositoryRepository;
import com.freightfox.githubsearcher.service.GithubSearchService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link GithubSearchService}.
 *
 * Design notes:
 * <ul>
 *   <li>Follows the Service layer pattern - orchestrates the client and repository.</li>
 *   <li>Uses upsert semantics: existing repositories (matched by GitHub id) are updated,
 *       new ones are inserted. No duplicates are ever created.</li>
 *   <li>Empty GitHub results are handled gracefully - an empty list is persisted and
 *       returned with an informative message.</li>
 *   <li>All repository writes run in a single transaction for efficiency.</li>
 * </ul>
 */
@Service
public class GithubSearchServiceImpl implements GithubSearchService {

    private static final Logger log = LoggerFactory.getLogger(GithubSearchServiceImpl.class);

    private static final String DEFAULT_SORT = "stars";
    private static final String SUCCESS_MESSAGE = "Repositories fetched and saved successfully";
    private static final String EMPTY_MESSAGE = "No repositories found matching the given criteria";

    private final GithubClient githubClient;
    private final RepositoryRepository repositoryRepository;

    public GithubSearchServiceImpl(GithubClient githubClient,
                                   RepositoryRepository repositoryRepository) {
        this.githubClient = githubClient;
        this.repositoryRepository = repositoryRepository;
    }

    @Override
    @Transactional
    public SearchResponse searchAndSave(SearchRequestDto request) {
        String sort = resolveSort(request.getSort());

        log.info("Searching GitHub repositories: query='{}', language='{}', sort='{}'",
                request.getQuery(), request.getLanguage(), sort);

        GithubSearchResponse githubResponse;
        try {
            githubResponse = githubClient.searchRepositories(request.getQuery(), request.getLanguage(), sort);
        } catch (GithubApiException ex) {
            log.warn("GitHub API call failed: {}", ex.getMessage());
            throw ex; // let the global exception handler produce the error response
        }

        List<GithubSearchResponse.GithubItem> items = githubResponse.getItems() == null
                ? List.of()
                : githubResponse.getItems();

        LocalDateTime fetchedAt = LocalDateTime.now();
        List<Repository> toSave = new ArrayList<>();
        int updated = 0;
        int inserted = 0;

        for (GithubSearchResponse.GithubItem item : items) {
            Repository entity = mapToEntity(item, fetchedAt);
            Repository existing = repositoryRepository.findByGithubId(entity.getGithubId()).orElse(null);
            if (existing != null) {
                existing.updateFrom(entity);
                existing.setId(existing.getId()); // preserve PK
                toSave.add(existing);
                updated++;
            } else {
                toSave.add(entity);
                inserted++;
            }
        }

        if (!toSave.isEmpty()) {
            repositoryRepository.saveAll(toSave);
            repositoryRepository.flush();
        }

        log.info("Upsert completed: inserted={}, updated={}, total={}",
                inserted, updated, toSave.size());

        List<RepositoryResponse> responses = toSave.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        String message = responses.isEmpty() ? EMPTY_MESSAGE : SUCCESS_MESSAGE;
        return new SearchResponse(message, responses);
    }

    private String resolveSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return DEFAULT_SORT;
        }
        String s = sort.trim().toLowerCase();
        if (!s.equals("stars") && !s.equals("forks") && !s.equals("updated")) {
            return DEFAULT_SORT;
        }
        return s;
    }

    private Repository mapToEntity(GithubSearchResponse.GithubItem item, LocalDateTime fetchedAt) {
        String owner = item.getOwner() != null ? item.getOwner().getLogin() : null;
        LocalDateTime lastUpdated = parseLastUpdated(item.getUpdatedAt());
        return new Repository(
                item.getId(),
                item.getName(),
                item.getDescription(),
                owner,
                item.getLanguage(),
                toLong(item.getStargazersCount()),
                toLong(item.getForksCount()),
                lastUpdated,
                fetchedAt
        );
    }

    private LocalDateTime parseLastUpdated(String updatedAt) {
        if (updatedAt == null || updatedAt.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(updatedAt).toLocalDateTime();
        } catch (Exception ex) {
            log.warn("Unable to parse GitHub updated_at '{}': {}", updatedAt, ex.getMessage());
            return null;
        }
    }

    private Long toLong(Integer value) {
        return value != null ? value.longValue() : null;
    }

    private RepositoryResponse toResponse(Repository repository) {
        return new RepositoryResponse(
                repository.getId(),
                repository.getName(),
                repository.getDescription(),
                repository.getOwnerName(),
                repository.getLanguage(),
                repository.getStars(),
                repository.getForks(),
                repository.getLastUpdated()
        );
    }
}
