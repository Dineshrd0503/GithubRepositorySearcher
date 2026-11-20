package com.freightfox.githubsearcher.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.freightfox.githubsearcher.dto.ErrorResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

/**
 * Pure unit tests for {@link GlobalExceptionHandler}.
 *
 * <p>Invokes each handler method directly with a mocked {@link WebRequest} and asserts the
 * structured {@link ErrorResponse} body and HTTP status, without needing a servlet container.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private WebRequest request(String path) {
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=" + path);
        return request;
    }

    @Test
    @DisplayName("should map GithubApiException to its HTTP status with structured body")
    void githubApiException_mappedToStatus() {
        GithubApiException ex = new GithubApiException("GitHub boom",
                HttpStatusCode.valueOf(502), null);

        ResponseEntityStub<ErrorResponse> result = invoke(() -> handler.handleGithubApi(ex, request("/api/github/search")));

        assertThat(result.statusCode().value()).isEqualTo(502);
        assertThat(result.body().getStatus()).isEqualTo(502);
        assertThat(result.body().getError()).isEqualTo(HttpStatusCode.valueOf(502).toString());
        assertThat(result.body().getMessage()).isEqualTo("GitHub boom");
        assertThat(result.body().getTimestamp()).isNotNull();
        assertThat(result.body().getPath()).isEqualTo("/api/github/search");
    }

    @Test
    @DisplayName("should map ResourceNotFoundException to 404")
    void notFound_mappedTo404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("repository not found");

        ResponseEntityStub<ErrorResponse> result =
                invoke(() -> handler.handleNotFound(ex, request("/api/github/repositories/999")));

        assertThat(result.statusCode().value()).isEqualTo(404);
        assertThat(result.body().getStatus()).isEqualTo(404);
        assertThat(result.body().getError()).isEqualTo("Not Found");
        assertThat(result.body().getMessage()).isEqualTo("repository not found");
    }

    @Test
    @DisplayName("should map HttpClientErrorException (403) from upstream")
    void clientError_mapped() {
        org.springframework.web.client.HttpClientErrorException ex =
                new org.springframework.web.client.HttpClientErrorException(HttpStatusCode.valueOf(403));

        ResponseEntityStub<ErrorResponse> result = invoke(() -> handler.handleClientError(ex, request("/api/github/search")));

        assertThat(result.statusCode().value()).isEqualTo(403);
        assertThat(result.body().getStatus()).isEqualTo(403);
    }

    @Test
    @DisplayName("should map unexpected exceptions to 500")
    void unexpected_mappedTo500() {
        IllegalStateException ex = new IllegalStateException("boom");

        ResponseEntityStub<ErrorResponse> result = invoke(() -> handler.handleGeneric(ex, request("/api/github/search")));

        assertThat(result.statusCode().value()).isEqualTo(500);
        assertThat(result.body().getStatus()).isEqualTo(500);
        assertThat(result.body().getError()).isEqualTo("Internal Server Error");
        assertThat(result.body().getMessage()).contains("boom");
    }

    @Test
    @DisplayName("ErrorResponse builder should create a populated instance")
    void errorResponse_builder() {
        ErrorResponse body = ErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .message("Invalid request")
                .timestamp(LocalDateTime.now())
                .path("/api/test")
                .details(List.of("query: blank"))
                .build();
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getMessage()).isEqualTo("Invalid request");
        assertThat(body.getDetails()).containsExactly("query: blank");
    }

    // Tiny helper to capture the ResponseEntity returned by a handler method.
    private static <T> ResponseEntityStub<T> invoke(ThrowingSupplier<ResponseEntity<T>> supplier) {
        try {
            ResponseEntity<T> entity = supplier.get();
            return new ResponseEntityStub<>(entity.getStatusCode(), entity.getBody());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws Throwable;
    }

    private static record ResponseEntityStub<T>(org.springframework.http.HttpStatusCode statusCode, T body) {
    }
}
