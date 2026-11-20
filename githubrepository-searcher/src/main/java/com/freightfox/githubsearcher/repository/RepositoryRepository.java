package com.freightfox.githubsearcher.repository;

import com.freightfox.githubsearcher.entity.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Repository} entities.
 *
 * Provides the upsert lookup by GitHub id and filtered retrieval used by the service layer.
 */
public interface RepositoryRepository extends JpaRepository<Repository, Long>, JpaSpecificationExecutor<Repository> {

    Optional<Repository> findByGithubId(Long githubId);

    boolean existsByGithubId(Long githubId);

    /**
     * Retrieves repositories filtered by an optional language and optional minimum star count.
     * Sorting is applied dynamically by the service layer through {@link
     * org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification,
     * org.springframework.data.domain.Sort)}.
     */
    @Query("SELECT r FROM Repository r WHERE (:language IS NULL OR LOWER(r.language) = LOWER(:language)) " +
           "AND (:minStars IS NULL OR r.stars >= :minStars)")
    List<Repository> findFiltered(@Param("language") String language, @Param("minStars") Long minStars);
}
