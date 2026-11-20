package com.freightfox.githubsearcher.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freightfox.githubsearcher.dto.GithubSearchResponse;
import com.freightfox.githubsearcher.exception.GithubApiException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Client for the GitHub REST API ("search repositories" endpoint).
 *
 * <p>Delegates HTTP calls to a pre-configured {@link RestTemplate} (which already sends the
 * required User-Agent / Accept headers). Translates transport and HTTP errors into
 * {@link GithubApiException} so that the service/controller layers deal with a single,
 * domain-friendly exception type.
 *
 * @see <a href="https://docs.github.com/en/rest/search#search-repositories">GitHub Search Repositories</a>
 */
@Component
public class GithubClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String searchPath;
    private final int perPage;

    public GithubClient(RestTemplate restTemplate,
                        ObjectMapper objectMapper,
                        @Value("${github.api.base-url}") String baseUrl,
                        @Value("${github.api.search-repositories}") String searchPath,
                        @Value("${github.api.per-page:30}") int perPage) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
        this.searchPath = searchPath;
        this.perPage = perPage;
    }

    /**
     * Searches GitHub repositories.
     *
     * @param query   repository name / keywords (partial or full match)
     * @param language optional programming language filter
     * @param sort    one of stars, forks, updated
     * @return the deserialised GitHub search response (never {@code null}; items may be empty)
     * @throws GithubApiException on transport failure, non-2xx responses, rate limiting,
     *                            or unparseable payloads
     */
    public GithubSearchResponse searchRepositories(String query, String language, String sort) {
        String q = buildQuery(query, language);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + searchPath)
                .queryParam("q", q)
                .queryParam("sort", sort)
                .queryParam("order", "desc")
                .queryParam("per_page", perPage);

        try {
            String json = restTemplate.getForObject(builder.toUriString(), String.class);
            if (json == null || json.isBlank()) {
                return emptyResponse();
            }
            return objectMapper.readValue(json, GithubSearchResponse.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new GithubApiException(
                    "GitHub API request failed (HTTP " + ex.getStatusCode() + "): " + ex.getMessage(),
                    ex.getStatusCode(), ex);
        } catch (RestClientException ex) {
            throw new GithubApiException("GitHub API request failed: " + ex.getMessage(), null, ex);
        } catch (Exception ex) {
            throw new GithubApiException("Failed to parse GitHub API response: " + ex.getMessage(), ex);
        }
    }

    private String buildQuery(String query, String language) {
        StringBuilder q = new StringBuilder(query.trim());
        if (language != null && !language.isBlank()) {
            q.append(" language:").append(language.trim());
        }
        return q.toString();
    }

    private GithubSearchResponse emptyResponse() {
        GithubSearchResponse response = new GithubSearchResponse();
        response.setTotalCount(0);
        response.setIncompleteResults(false);
        response.setItems(List.of());
        return response;
    }
}
