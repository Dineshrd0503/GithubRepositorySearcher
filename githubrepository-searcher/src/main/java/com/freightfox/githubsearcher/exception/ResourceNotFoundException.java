package com.freightfox.githubsearcher.exception;

/**
 * Thrown when a requested resource cannot be found (for example, a repository that does
 * not exist in the database when a future lookup-by-id endpoint is added).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
