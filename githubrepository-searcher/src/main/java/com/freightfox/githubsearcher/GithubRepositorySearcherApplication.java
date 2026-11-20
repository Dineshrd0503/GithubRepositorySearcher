package com.freightfox.githubsearcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the GitHub Repository Searcher application.
 *
 * <p>The application:
 * <ul>
 *   <li>Searches GitHub repositories via the GitHub REST API ({@code POST /api/github/search})</li>
 *   <li>Persists search results into PostgreSQL with upsert semantics (no duplicates)</li>
 *   <li>Retrieves stored repositories with optional filters ({@code GET /api/github/repositories})</li>
 *   <li>Exposes interactive API documentation via Swagger UI at {@code /swagger-ui.html}</li>
 * </ul>
 */
@SpringBootApplication(scanBasePackages = "com.freightfox.githubsearcher")
public class GithubRepositorySearcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(GithubRepositorySearcherApplication.class, args);
    }
}
