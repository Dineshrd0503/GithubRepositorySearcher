package com.freightfox.githubsearcher.controller;

import com.freightfox.githubsearcher.dto.RepositoryResponse;
import com.freightfox.githubsearcher.dto.ErrorResponse;
import com.freightfox.githubsearcher.service.RepositoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for retrieving stored GitHub repositories from PostgreSQL.
 *
 * Endpoint: {@code GET /api/github/repositories}
 */
@RestController
@RequestMapping("/api/github/repositories")
@Tag(name = "Stored Repositories", description = "Retrieve stored repository results with optional filters")
public class RepositoryController {

    private final RepositoryService repositoryService;

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @Operation(summary = "Retrieve stored repositories",
            description = "Returns stored repository details filtered by optional programming language, "
                    + "minimum stars, and sorted by stars / forks / updated date (default stars).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stored repositories",
                    content = @Content(schema = @Schema(implementation = RepositoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<RepositoryResponse>> getStored(
            @Parameter(description = "Filter by programming language (case-insensitive)", example = "Java")
            @RequestParam(required = false) String language,

            @Parameter(description = "Filter by programming language (alternative casing)", example = "Java", hidden = true)
            @RequestParam(required = false) String LANGUAGE,

            @Parameter(description = "Minimum stars count", example = "100")
            @RequestParam(required = false) Long minStars,

            @Parameter(description = "Minimum stars count (alternative casing)", example = "100", hidden = true)
            @RequestParam(required = false) Long minstars,

            @Parameter(description = "Sort field: stars, forks, updated (default stars)", example = "stars")
            @RequestParam(required = false, defaultValue = "stars") String sort,

            @Parameter(description = "Sort field (alternative casing)", example = "stars", hidden = true)
            @RequestParam(required = false) String SORT) {
        // Spring's @RequestParam is case-sensitive, so accept all casings for each parameter name
        String effectiveLanguage = (language != null) ? language : LANGUAGE;
        Long effectiveMinStars = (minStars != null) ? minStars : minstars;
        String effectiveSort = (sort != null) ? sort : SORT;
        List<RepositoryResponse> repositories = repositoryService.findStored(effectiveLanguage, effectiveMinStars, effectiveSort);
        return ResponseEntity.ok(repositories);
    }
}
