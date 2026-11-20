package com.freightfox.githubsearcher.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.freightfox.githubsearcher.client.GithubClient;
import com.freightfox.githubsearcher.dto.GithubSearchResponse;
import com.freightfox.githubsearcher.dto.RepositoryResponse;
import com.freightfox.githubsearcher.dto.SearchRequestDto;
import com.freightfox.githubsearcher.dto.SearchResponse;
import com.freightfox.githubsearcher.entity.Repository;
import com.freightfox.githubsearcher.repository.RepositoryRepository;
import com.freightfox.githubsearcher.service.impl.GithubSearchServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link GithubSearchServiceImpl}.
 *
 * <p>Covers: inserting new repositories, upserting existing ones (update instead of
 * duplicate), empty GitHub results, invalid sort falling back to default, and transactional
 * persistence.
 */
@ExtendWith(MockitoExtension.class)
class GithubSearchServiceTest {

    @Mock
    private GithubClient githubClient;

    @Mock
    private RepositoryRepository repositoryRepository;

    private GithubSearchServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new GithubSearchServiceImpl(githubClient, repositoryRepository);
    }

    @Test
    @DisplayName("should insert new repositories and return success message")
    void searchAndSave_insertsNewRepositories() {
        SearchRequestDto request = new SearchRequestDto("spring boot", "Java", "stars");
        GithubSearchResponse githubResponse = githubResponseWith(
                newRepo(1L, "repo-one", "desc one", "owner1", "Java", 450L, 120L,
                        "2024-01-01T12:00:00Z"),
                newRepo(2L, "repo-two", "desc two", "owner2", "Java", 300L, 50L,
                        "2024-02-01T12:00:00Z"));
        when(githubClient.searchRepositories("spring boot", "Java", "stars")).thenReturn(githubResponse);
        when(repositoryRepository.findByGithubId(1L)).thenReturn(Optional.empty());
        when(repositoryRepository.findByGithubId(2L)).thenReturn(Optional.empty());

        SearchResponse response = service.searchAndSave(request);

        assertThat(response.getMessage()).isEqualTo("Repositories fetched and saved successfully");
        assertThat(response.getRepositories()).hasSize(2);
        assertThat(response.getRepositories().get(0).getName()).isEqualTo("repo-one");
        assertThat(response.getRepositories().get(0).getStars()).isEqualTo(450L);

        ArgumentCaptor<List<Repository>> captor = ArgumentCaptor.forClass(List.class);
        verify(repositoryRepository).saveAll(captor.capture());
        verify(repositoryRepository).flush();
        assertThat(captor.getValue()).hasSize(2);
        assertThat(captor.getValue().get(0).getGithubId()).isEqualTo(1L);
        assertThat(captor.getValue().get(0).getFetchedAt()).isNotNull();
    }

    @Test
    @DisplayName("should update existing repository instead of duplicating (upsert)")
    void searchAndSave_updatesExistingRepository() {
        SearchRequestDto request = new SearchRequestDto("spring boot", null, "stars");
        GithubSearchResponse githubResponse = githubResponseWith(
                newRepo(1L, "repo-one", "updated desc", "owner1", "Java", 999L, 10L,
                        "2025-01-01T12:00:00Z"));
        when(githubClient.searchRepositories("spring boot", null, "stars")).thenReturn(githubResponse);

        Repository existing = new Repository();
        existing.setId(100L);
        existing.setGithubId(1L);
        existing.setName("repo-one");
        existing.setStars(1L);
        when(repositoryRepository.findByGithubId(1L)).thenReturn(Optional.of(existing));

        SearchResponse response = service.searchAndSave(request);

        assertThat(response.getMessage()).isEqualTo("Repositories fetched and saved successfully");
        assertThat(response.getRepositories()).hasSize(1);
        assertThat(response.getRepositories().get(0).getStars()).isEqualTo(999L);

        ArgumentCaptor<List<Repository>> captor = ArgumentCaptor.forClass(List.class);
        verify(repositoryRepository).saveAll(captor.capture());
        List<Repository> saved = captor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getId()).isEqualTo(100L); // PK preserved
        assertThat(saved.get(0).getStars()).isEqualTo(999L); // updated
        assertThat(saved.get(0).getName()).isEqualTo("repo-one");
    }

    @Test
    @DisplayName("should return empty message when GitHub returns no results")
    void searchAndSave_emptyResults() {
        SearchRequestDto request = new SearchRequestDto("zzz-nothing-matching", null, "stars");
        GithubSearchResponse empty = new GithubSearchResponse();
        empty.setTotalCount(0);
        empty.setIncompleteResults(false);
        empty.setItems(List.of());
        when(githubClient.searchRepositories("zzz-nothing-matching", null, "stars")).thenReturn(empty);

        SearchResponse response = service.searchAndSave(request);

        assertThat(response.getMessage()).isEqualTo("No repositories found matching the given criteria");
        assertThat(response.getRepositories()).isEmpty();
        verify(repositoryRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("should fall back to default sort when an invalid sort is provided")
    void searchAndSave_invalidSortDefaultsToStars() {
        SearchRequestDto request = new SearchRequestDto("spring boot", null, "invalid-sort");
        GithubSearchResponse empty = new GithubSearchResponse();
        empty.setTotalCount(0);
        empty.setItems(List.of());
        when(githubClient.searchRepositories("spring boot", null, "stars")).thenReturn(empty);

        service.searchAndSave(request);

        // Verify the client was invoked with the default sort 'stars', not the invalid value.
        org.mockito.Mockito.verify(githubClient)
                .searchRepositories("spring boot", null, "stars");
    }

    private GithubSearchResponse githubResponseWith(GithubSearchResponse.GithubItem... items) {
        GithubSearchResponse response = new GithubSearchResponse();
        response.setTotalCount(items.length);
        response.setIncompleteResults(false);
        response.setItems(List.of(items));
        return response;
    }

    private GithubSearchResponse.GithubItem newRepo(Long id, String name, String description,
                                                    String owner, String language, Long stars,
                                                    Long forks, String updatedAt) {
        GithubSearchResponse.GithubItem item = new GithubSearchResponse.GithubItem();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        GithubSearchResponse.GithubItem.Owner o = new GithubSearchResponse.GithubItem.Owner();
        o.setLogin(owner);
        item.setOwner(o);
        item.setLanguage(language);
        item.setStargazersCount(stars.intValue());
        item.setForksCount(forks.intValue());
        item.setUpdatedAt(updatedAt);
        return item;
    }

    private Repository newEntity(Long githubId, String name, String language, Long stars) {
        Repository r = new Repository();
        r.setGithubId(githubId);
        r.setName(name);
        r.setLanguage(language);
        r.setStars(stars);
        r.setLastUpdated(LocalDateTime.now());
        return r;
    }

    private List<RepositoryResponse> responsesOf(Repository... repos) {
        return List.of(repos).stream()
                .map(r -> new RepositoryResponse(r.getId(), r.getName(), r.getDescription(),
                        r.getOwnerName(), r.getLanguage(), r.getStars(), r.getForks(),
                        r.getLastUpdated()))
                .toList();
    }
}
