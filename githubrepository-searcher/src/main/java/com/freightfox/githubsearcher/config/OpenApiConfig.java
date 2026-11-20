package com.freightfox.githubsearcher.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger configuration.
 *
 * <p>Defines the global API metadata (title, version, description) and the default
 * server URL so that the generated OpenAPI document and Swagger UI are accurate.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GitHub Repository Searcher API")
                        .version("1.0.0")
                        .description("""
                                Backend assignment: GitHub Repository Searcher.

                                Endpoints:
                                - POST /api/github/search  - search GitHub repositories and persist results to PostgreSQL
                                - GET  /api/github/repositories - retrieve stored repositories with optional filters
                                """))
                .servers(List.of(new Server().url("http://localhost:8080")
                        .description("Development server")));
    }
}
