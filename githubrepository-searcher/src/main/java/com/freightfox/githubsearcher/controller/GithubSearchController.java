package com.freightfox.githubsearcher.controller;

import com.freightfox.githubsearcher.dto.RepositoryResponse;
import com.freightfox.githubsearcher.dto.SearchRequestDto;
import com.freightfox.githubsearcher.dto.SearchResponse;
import com.freightfox.githubsearcher.service.GithubSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for searching GitHub repositories and persisting the results.
 *
 * Endpoint: {@code POST /api/github/search}
 */
@RestController
@RequestMapping("/api/github/search")
@Tag(name = "GitHub Search", description = "Search GitHub repositories and store results in PostgreSQL")
public class GithubSearchController {

    private final GithubSearchService githubSearchService;

    public GithubSearchController(GithubSearchService githubSearchService) {
        this.githubSearchService = githubSearchService;
    }

    @Operation(summary = "Search GitHub repositories and persist results",
            description = "Searches GitHub repositories by name/keywords, optional programming language and sort order. "
                    + "Results are upserted into PostgreSQL (no duplicates).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Repositories fetched and saved",
                    content = @Content(schema = @Schema(implementation = SearchResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = com.freightfox.githubsearcher.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "GitHub API error / rate limited",
                    content = @Content(schema = @Schema(implementation = com.freightfox.githubsearcher.dto.ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<SearchResponse> search(
            @Parameter(description = "Search request body", required = true,
                    schema = @Schema(implementation = SearchRequestDto.class))
            @Valid @RequestBody SearchRequestDto request) {
        SearchResponse response = githubSearchService.searchAndSave(request);
        return ResponseEntity.ok(response);
    }
}
