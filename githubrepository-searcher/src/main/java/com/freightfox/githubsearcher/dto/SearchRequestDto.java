package com.freightfox.githubsearcher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request body for {@code POST /api/github/search}.
 *
 * <pre>
 * {
 *   "query": "spring boot",
 *   "language": "Java",
 *   "sort": "stars"
 * }
 * </pre>
 */
public class SearchRequestDto {

    @NotBlank(message = "query is required and must not be blank")
    @Size(max = 200, message = "query must not exceed 200 characters")
    private String query;

    @Size(max = 100, message = "language must not exceed 100 characters")
    private String language;

    @Pattern(regexp = "^(stars|forks|updated)$", message = "sort must be one of: stars, forks, updated")
    private String sort = "stars";

    public SearchRequestDto() {
    }

    public SearchRequestDto(String query, String language, String sort) {
        this.query = query;
        this.language = language;
        this.sort = sort;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
