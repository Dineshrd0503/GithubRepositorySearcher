package com.freightfox.githubsearcher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Deserialises the GitHub "search repositories" API response.
 *
 * See https://docs.github.com/en/rest/search#search-repositories
 */
public class GithubSearchResponse {

    @JsonProperty("total_count")
    private int totalCount;

    @JsonProperty("incomplete_results")
    private boolean incompleteResults;

    private List<GithubItem> items;

    public GithubSearchResponse() {
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isIncompleteResults() {
        return incompleteResults;
    }

    public void setIncompleteResults(boolean incompleteResults) {
        this.incompleteResults = incompleteResults;
    }

    public List<GithubItem> getItems() {
        return items;
    }

    public void setItems(List<GithubItem> items) {
        this.items = items;
    }

    /**
     * A single repository item returned by the GitHub search API.
     */
    public static class GithubItem {

        private Long id;
        private String name;

        @JsonProperty("full_name")
        private String fullName;

        private String description;

        private Owner owner;

        @JsonProperty("language")
        private String language;

        @JsonProperty("stargazers_count")
        private Integer stargazersCount;

        @JsonProperty("forks_count")
        private Integer forksCount;

        @JsonProperty("updated_at")
        private String updatedAt;

        public GithubItem() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Owner getOwner() {
            return owner;
        }

        public void setOwner(Owner owner) {
            this.owner = owner;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public Integer getStargazersCount() {
            return stargazersCount;
        }

        public void setStargazersCount(Integer stargazersCount) {
            this.stargazersCount = stargazersCount;
        }

        public Integer getForksCount() {
            return forksCount;
        }

        public void setForksCount(Integer forksCount) {
            this.forksCount = forksCount;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        /**
         * Repository owner as returned by GitHub.
         */
        public static class Owner {

            private String login;

            public Owner() {
            }

            public String getLogin() {
                return login;
            }

            public void setLogin(String login) {
                this.login = login;
            }
        }
    }
}
