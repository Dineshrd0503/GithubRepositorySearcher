package com.freightfox.githubsearcher.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.freightfox.githubsearcher.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global, REST-compliant exception handler.
 *
 * <p>All exceptions are mapped to a structured {@link com.freightfox.githubsearcher.dto.ErrorResponse}
 * body with an appropriate HTTP status code, so API consumers (e.g. Postman) receive
 * predictable, machine-readable error messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        if (details.isEmpty()) {
            details = ex.getBindingResult().getGlobalErrors().stream()
                    .map(globalError -> globalError.getObjectName() + ": " + globalError.getDefaultMessage())
                    .collect(Collectors.toList());
        }
        return build(ErrorResponse.builder()
                .status(HttpStatusCode.valueOf(400).value())
                .error("Validation failed")
                .message("Invalid request payload")
                .timestamp(LocalDateTime.now())
                .path(getPath(request))
                .details(details)
                .build(), HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(v -> (v.getPropertyPath() != null ? v.getPropertyPath() : "value") + ": " + v.getMessage())
                .collect(Collectors.toList());
        return build(ErrorResponse.builder()
                .status(HttpStatusCode.valueOf(400).value())
                .error("Constraint violation")
                .message("Invalid request parameter")
                .timestamp(LocalDateTime.now())
                .path(getPath(request))
                .details(details)
                .build(), HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler(GithubApiException.class)
    public ResponseEntity<ErrorResponse> handleGithubApi(GithubApiException ex, WebRequest request) {
        HttpStatusCode status = ex.getStatus() != null ? ex.getStatus() : HttpStatusCode.valueOf(502);
        return build(ErrorResponse.builder()
                .status(status.value())
                .error(status.toString())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(getPath(request))
                .details(Collections.singletonList(ex.getMessage()))
                .build(), status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        return build(ErrorResponse.builder()
                .status(HttpStatusCode.valueOf(404).value())
                .error("Not Found")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(getPath(request))
                .build(), HttpStatusCode.valueOf(404));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleClientError(HttpClientErrorException ex, WebRequest request) {
        return build(ErrorResponse.builder()
                .status(ex.getStatusCode().value())
                .error(ex.getStatusText())
                .message("Client error: " + ex.getResponseBodyAsString())
                .timestamp(LocalDateTime.now())
                .path(getPath(request))
                .build(), ex.getStatusCode());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleServerError(HttpServerErrorException ex, WebRequest request) {
        return build(ErrorResponse.builder()
                .status(ex.getStatusCode().value())
                .error(ex.getStatusText())
                .message("Upstream server error: " + ex.getResponseBodyAsString())
                .timestamp(LocalDateTime.now())
                .path(getPath(request))
                .build(), ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, WebRequest request) {
        return build(ErrorResponse.builder()
                .status(HttpStatusCode.valueOf(500).value())
                .error("Internal Server Error")
                .message("An unexpected error occurred: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(getPath(request))
                .build(), HttpStatusCode.valueOf(500));
    }

    private ResponseEntity<ErrorResponse> build(ErrorResponse body, HttpStatusCode status) {
        return ResponseEntity.status(status).body(body);
    }

    private String getPath(WebRequest request) {
        return request != null && request.getDescription(false) != null
                ? request.getDescription(false).replace("uri=", "")
                : "unknown";
    }
}
