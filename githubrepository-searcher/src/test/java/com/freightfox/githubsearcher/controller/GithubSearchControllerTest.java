package com.freightfox.githubsearcher.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.freightfox.githubsearcher.dto.ErrorResponse;
import com.freightfox.githubsearcher.dto.SearchRequestDto;
import com.freightfox.githubsearcher.dto.SearchResponse;
import com.freightfox.githubsearcher.exception.GlobalExceptionHandler;
import com.freightfox.githubsearcher.service.GithubSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web layer tests for {@link GithubSearchController}.
 *
 * <p>Uses MockMvc to exercise {@code POST /api/github/search}, validating both the happy path
 * and validation error handling (blank query, invalid sort).
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GithubSearchController.class)
@Import(GlobalExceptionHandler.class)
class GithubSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GithubSearchService githubSearchService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("should return 200 and stored repositories for a valid search request")
    void search_validRequest_returnsOk() throws Exception {
        SearchRequestDto request = new SearchRequestDto("spring boot", "Java", "stars");
        SearchResponse response = new SearchResponse(
                "Repositories fetched and saved successfully", List.of());
        when(githubSearchService.searchAndSave(any())).thenReturn(response);

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Repositories fetched and saved successfully"))
                .andExpect(jsonPath("$.repositories").isArray());
    }

    @Test
    @DisplayName("should return 400 when query is blank")
    void search_blankQuery_returnsBadRequest() throws Exception {
        SearchRequestDto request = new SearchRequestDto("   ", "Java", "stars");

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid request payload"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("should return 400 when query is missing")
    void search_missingQuery_returnsBadRequest() throws Exception {
        SearchRequestDto request = new SearchRequestDto(null, "Java", "stars");

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 when sort value is invalid")
    void search_invalidSort_returnsBadRequest() throws Exception {
        SearchRequestDto request = new SearchRequestDto("spring boot", null, "invalid");

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should propagate GitHub API errors as 502")
    void search_githubApiError_returns502() throws Exception {
        SearchRequestDto request = new SearchRequestDto("spring boot", null, "stars");
        when(githubSearchService.searchAndSave(any()))
                .thenThrow(new RuntimeException("GitHub API rate limit exceeded"));

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }
}
