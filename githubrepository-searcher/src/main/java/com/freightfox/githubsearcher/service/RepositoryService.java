package com.freightfox.githubsearcher.service;

import com.freightfox.githubsearcher.dto.RepositoryResponse;
import java.util.List;

/**
 * Service contract for retrieving stored repositories from PostgreSQL.
 */
public interface RepositoryService {

    /**
     * Retrieves stored repositories, optionally filtered by programming language and/or
     * minimum star count, and sorted by stars, forks, or last updated date.
     *
     * @param language optional programming language filter (case-insensitive)
     * @param minStars optional minimum stars count
     * @param sort     sort field: stars | forks | updated (defaults to stars)
     * @return list of stored repository responses
     */
    List<RepositoryResponse> findStored(String language, Long minStars, String sort);
}
