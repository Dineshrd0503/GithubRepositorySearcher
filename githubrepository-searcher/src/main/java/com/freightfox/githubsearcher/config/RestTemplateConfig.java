package com.freightfox.githubsearcher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for the {@link RestTemplate} used to call the GitHub REST API.
 *
 * <p>GitHub requires a User-Agent header on every request, so it is added here
 * globally along with the Accept header for the GitHub JSON representation.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate githubRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((ClientHttpRequestInterceptor) (request, body, execution) -> {
            request.getHeaders().add(HttpHeaders.USER_AGENT, "githubrepository-searcher/1.0");
            request.getHeaders().add(HttpHeaders.ACCEPT, "application/vnd.github+json");
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
