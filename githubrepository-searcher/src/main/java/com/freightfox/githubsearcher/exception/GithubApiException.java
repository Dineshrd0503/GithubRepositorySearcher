package com.freightfox.githubsearcher.exception;

import org.springframework.http.HttpStatusCode;

/**
 * Business/technical exception thrown when interacting with the GitHub API or when the
 * application cannot fulfil a search request. Carries an HTTP status so the controller
 * advice can map it to the correct response code.
 */
public class GithubApiException extends RuntimeException {

    private final HttpStatusCode status;

    public GithubApiException(String message) {
        this(message, HttpStatusCode.valueOf(502), null);
    }

    public GithubApiException(String message, Throwable cause) {
        this(message, HttpStatusCode.valueOf(502), cause);
    }

    public GithubApiException(String message, HttpStatusCode status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatusCode getStatus() {
        return status;
    }
}
