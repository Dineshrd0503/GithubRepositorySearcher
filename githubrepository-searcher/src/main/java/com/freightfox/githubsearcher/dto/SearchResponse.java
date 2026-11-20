package com.freightfox.githubsearcher.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response returned by {@code POST /api/github/search}.
 */
public class SearchResponse {

    private String message;
    private List<RepositoryResponse> repositories;

    public SearchResponse() {
    }

    public SearchResponse(String message, List<RepositoryResponse> repositories) {
        this.message = message;
        this.repositories = repositories;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RepositoryResponse> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<RepositoryResponse> repositories) {
        this.repositories = repositories;
    }
}
