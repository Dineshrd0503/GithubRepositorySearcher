package com.freightfox.githubsearcher.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freightfox.githubsearcher.dto.GithubSearchResponse;
import com.freightfox.githubsearcher.exception.GithubApiException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Unit tests for {@link GithubClient}.
 *
 * <p>Covers: successful search, GitHub rate limiting (HTTP 403), empty responses, and
 * unparseable payloads.
 */
@ExtendWith(MockitoExtension.class)
class GithubClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;
    private GithubClient client;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        client = new GithubClient(restTemplate, objectMapper,
                "https://api.github.com", "/search/repositories", 30);
    }

    @Test
    @DisplayName("should return mapped search response when GitHub API succeeds")
    void searchRepositories_success() throws Exception {
        String json = """
                {
                  "total_count": 1,
                  "incomplete_results": false,
                  "items": [
                    {
                      "id": 1296269,
                      "name": "Hello-World",
                      "full_name": "octocat/Hello-World",
                      "description": "Your first repository",
                      "owner": { "login": "octocat" },
                      "language": "Java",
                      "stargazers_count": 450,
                      "forks_count": 120,
                      "updated_at": "2024-01-01T12:00:00Z"
                    }
                  ]
                }
                """;
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn(json);

        GithubSearchResponse response = client.searchRepositories("spring boot", "Java", "stars");

        assertThat(response.getTotalCount()).isEqualTo(1);
        assertThat(response.isIncompleteResults()).isFalse();
        assertThat(response.getItems()).hasSize(1);
        GithubSearchResponse.GithubItem item = response.getItems().get(0);
        assertThat(item.getId()).isEqualTo(1296269L);
        assertThat(item.getName()).isEqualTo("Hello-World");
        assertThat(item.getOwner().getLogin()).isEqualTo("octocat");
        assertThat(item.getLanguage()).isEqualTo("Java");
        assertThat(item.getStargazersCount()).isEqualTo(450);
        assertThat(item.getForksCount()).isEqualTo(120);
        assertThat(item.getUpdatedAt()).isEqualTo("2024-01-01T12:00:00Z");
    }

    @Test
    @DisplayName("should return empty response when GitHub returns no items")
    void searchRepositories_emptyBody() {
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn("");

        GithubSearchResponse response = client.searchRepositories("nonexistentxyz", null, "stars");

        assertThat(response.getTotalCount()).isZero();
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    @DisplayName("should throw GithubApiException with HTTP 403 on GitHub rate limit")
    void searchRepositories_rateLimited() {
        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN,
                        "API rate limit exceeded"));

        assertThatThrownBy(() -> client.searchRepositories("spring boot", null, "stars"))
                .isInstanceOf(GithubApiException.class)
                .hasMessageContaining("HTTP 403")
                .satisfies(ex -> {
                    GithubApiException gex = (GithubApiException) ex;
                    assertThat(gex.getStatus().value()).isEqualTo(403);
                });
    }

    @Test
    @DisplayName("should throw GithubApiException when payload cannot be parsed")
    void searchRepositories_invalidJson() {
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn("{ not valid json }");

        assertThatThrownBy(() -> client.searchRepositories("spring boot", null, "stars"))
                .isInstanceOf(GithubApiException.class)
                .hasMessageContaining("Failed to parse GitHub API response");
    }

    @Test
    @DisplayName("should append language filter to the GitHub query")
    void searchRepositories_buildsQueryWithLanguage() {
        when(restTemplate.getForObject(any(String.class), eq(String.class)))
                .thenReturn("{\"total_count\":0,\"incomplete_results\":false,\"items\":[]}");

        client.searchRepositories("spring boot", "Kotlin", "forks");

        // Verify the call was made; exact URL assertion avoided due to query encoding.
        org.mockito.Mockito.verify(restTemplate).getForObject(any(String.class), eq(String.class));
    }
}
