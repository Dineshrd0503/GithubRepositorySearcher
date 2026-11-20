package com.freightfox.githubsearcher.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.freightfox.githubsearcher.dto.RepositoryResponse;
import com.freightfox.githubsearcher.entity.Repository;
import com.freightfox.githubsearcher.repository.RepositoryRepository;
import com.freightfox.githubsearcher.service.impl.RepositoryServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * Unit tests for {@link RepositoryServiceImpl}.
 *
 * <p>Covers: retrieval with no filters, language filter, minimum stars filter, sort
 * resolution (stars / forks / updated / invalid defaults to stars), and empty results.
 */
@ExtendWith(MockitoExtension.class)
class RepositoryServiceTest {

    @Mock
    private RepositoryRepository repositoryRepository;

    private RepositoryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new RepositoryServiceImpl(repositoryRepository);
    }

    @Test
    @DisplayName("should return all stored repositories when no filters are provided")
    void findStored_noFilters() {
        when(repositoryRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(repo(1L, "repo-a", "Java", 450L)));

        List<RepositoryResponse> result = service.findStored(null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("repo-a");
        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(repositoryRepository).findAll(any(Specification.class), sortCaptor.capture());
        assertThat(sortCaptor.getValue().toString()).contains("stars");
    }

    @Test
    @DisplayName("should filter by programming language")
    void findStored_byLanguage() {
        when(repositoryRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(repo(1L, "repo-a", "Java", 450L)));

        service.findStored("Java", null, "stars");

        ArgumentCaptor<Specification<Repository>> specCaptor =
                ArgumentCaptor.forClass(Specification.class);
        verify(repositoryRepository).findAll(specCaptor.capture(), any(Sort.class));
        assertThat(specCaptor.getValue()).isNotNull();
    }

    @Test
    @DisplayName("should filter by minimum stars count")
    void findStored_byMinStars() {
        when(repositoryRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(repo(1L, "repo-a", "Java", 450L)));

        service.findStored(null, 100L, "stars");

        ArgumentCaptor<Specification<Repository>> specCaptor =
                ArgumentCaptor.forClass(Specification.class);
        verify(repositoryRepository).findAll(specCaptor.capture(), any(Sort.class));
        assertThat(specCaptor.getValue()).isNotNull();
    }

    @Test
    @DisplayName("should sort by forks when requested")
    void findStored_sortByForks() {
        when(repositoryRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of());

        service.findStored(null, null, "forks");

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(repositoryRepository).findAll(any(Specification.class), sortCaptor.capture());
        assertThat(sortCaptor.getValue().toString()).contains("forks");
    }

    @Test
    @DisplayName("should sort by updated date when requested")
    void findStored_sortByUpdated() {
        when(repositoryRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of());

        service.findStored(null, null, "updated");

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(repositoryRepository).findAll(any(Specification.class), sortCaptor.capture());
        assertThat(sortCaptor.getValue().toString()).contains("lastUpdated");
    }

    @Test
    @DisplayName("should default sort to stars when an invalid sort is provided")
    void findStored_invalidSortDefaultsToStars() {
        when(repositoryRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of());

        service.findStored(null, null, "nonsense");

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(repositoryRepository).findAll(any(Specification.class), sortCaptor.capture());
        assertThat(sortCaptor.getValue().toString()).contains("stars");
    }

    @Test
    @DisplayName("should return empty list when no repositories are stored")
    void findStored_empty() {
        when(repositoryRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of());

        assertThat(service.findStored("Java", 10L, "stars")).isEmpty();
    }

    @Test
    @DisplayName("should map entity fields to response DTO correctly")
    void findStored_mapsFields() {
        Repository r = repo(7L, "demo", "Java", 1000L);
        when(repositoryRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(r));

        List<RepositoryResponse> result = service.findStored(null, null, "stars");

        assertThat(result).hasSize(1);
        RepositoryResponse resp = result.get(0);
        assertThat(resp.getId()).isEqualTo(7L);
        assertThat(resp.getName()).isEqualTo("demo");
        assertThat(resp.getLanguage()).isEqualTo("Java");
        assertThat(resp.getStars()).isEqualTo(1000L);
        assertThat(resp.getLastUpdated()).isEqualTo(r.getLastUpdated());
    }

    private Repository repo(Long id, String name, String language, Long stars) {
        Repository r = new Repository();
        r.setId(id);
        r.setName(name);
        r.setGithubId(1000L + id);
        r.setDescription("description " + id);
        r.setOwnerName("owner" + id);
        r.setLanguage(language);
        r.setStars(stars);
        r.setForks(10L);
        r.setLastUpdated(LocalDateTime.parse("2024-01-01T12:00:00"));
        r.setFetchedAt(LocalDateTime.now());
        return r;
    }
}
