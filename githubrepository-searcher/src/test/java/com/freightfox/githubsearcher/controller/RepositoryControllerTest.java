package com.freightfox.githubsearcher.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.freightfox.githubsearcher.dto.RepositoryResponse;
import com.freightfox.githubsearcher.exception.GlobalExceptionHandler;
import com.freightfox.githubsearcher.service.RepositoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web layer tests for {@link RepositoryController}.
 *
 * <p>Uses MockMvc to exercise {@code GET /api/github/repositories} with optional filters
 * (language, minStars, sort).
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = RepositoryController.class)
@Import(GlobalExceptionHandler.class)
class RepositoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepositoryService repositoryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("should return filtered stored repositories with 200")
    void getStored_withFilters_returnsOk() throws Exception {
        RepositoryResponse repo = new RepositoryResponse(1L, "spring-boot-example",
                "An example repository for Spring Boot", "user123", "Java", 450L, 120L,
                LocalDateTime.parse("2024-01-01T12:00:00"));
        when(repositoryService.findStored("Java", 100L, "stars"))
                .thenReturn(List.of(repo));

        mockMvc.perform(get("/api/github/repositories")
                        .param("language", "Java")
                        .param("minStars", "100")
                        .param("sort", "stars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("spring-boot-example"))
                .andExpect(jsonPath("$[0].language").value("Java"))
                .andExpect(jsonPath("$[0].stars").value(450));
    }

    @Test
    @DisplayName("should return stored repositories when no filters are provided")
    void getStored_noFilters_returnsOk() throws Exception {
        when(repositoryService.findStored(null, null, "stars"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/github/repositories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("should use default sort stars when sort parameter is omitted")
    void getStored_omitSort_usesDefaultStars() throws Exception {
        when(repositoryService.findStored(any(), any(), org.mockito.ArgumentMatchers.eq("stars")))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/github/repositories"))
                .andExpect(status().isOk());
    }
}
