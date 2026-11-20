package com.freightfox.githubsearcher.service.impl;

import com.freightfox.githubsearcher.dto.RepositoryResponse;
import com.freightfox.githubsearcher.entity.Repository;
import com.freightfox.githubsearcher.repository.RepositoryRepository;
import com.freightfox.githubsearcher.service.RepositoryService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Implementation of {@link RepositoryService}.
 *
 * <p>Filtering is pushed to the database via a JPA {@link Specification}, and sorting is
 * applied at the database level through Spring Data {@link Sort}, keeping repository
 * retrieval efficient even as the stored data grows.
 */
@Service
public class RepositoryServiceImpl implements RepositoryService {

    private static final String DEFAULT_SORT = "stars";

    private final RepositoryRepository repositoryRepository;

    public RepositoryServiceImpl(RepositoryRepository repositoryRepository) {
        this.repositoryRepository = repositoryRepository;
    }

    @Override
    public List<RepositoryResponse> findStored(String language, Long minStars, String sort) {
        String resolvedSort = resolveSort(sort);

        Specification<Repository> spec = Specification.where(null);
        if (StringUtils.hasText(language)) {
            String lang = language.trim().toLowerCase();
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("language")), lang));
        }
        if (minStars != null) {
            spec = spec.and((root, query, cb) -> cb.ge(root.get("stars"), minStars));
        }

        Sort springSort = switch (resolvedSort) {
            case "forks" -> Sort.by(Sort.Order.desc("forks"));
            case "updated" -> Sort.by(Sort.Order.desc("lastUpdated"));
            default -> Sort.by(Sort.Order.desc("stars"));
        };

        List<Repository> repositories = repositoryRepository.findAll(spec, springSort);

        return repositories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private String resolveSort(String sort) {
        if (!StringUtils.hasText(sort)) {
            return DEFAULT_SORT;
        }
        String s = sort.trim().toLowerCase();
        if (!s.equals("stars") && !s.equals("forks") && !s.equals("updated")) {
            return DEFAULT_SORT;
        }
        return s;
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
