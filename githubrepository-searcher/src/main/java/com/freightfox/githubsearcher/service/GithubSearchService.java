package com.freightfox.githubsearcher.service;

import com.freightfox.githubsearcher.dto.SearchRequestDto;
import com.freightfox.githubsearcher.dto.SearchResponse;

/**
 * Service contract for searching GitHub repositories and persisting the results.
 */
public interface GithubSearchService {

    /**
     * Searches GitHub for repositories matching the given criteria and saves/updates them
     * in PostgreSQL using upsert semantics (no duplicates).
     *
     * @param request validated search criteria
     * @response the response containing the persisted repositories and a status message
     */
    SearchResponse searchAndSave(SearchRequestDto request);
}
