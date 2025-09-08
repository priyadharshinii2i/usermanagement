package com.userservice.exception;

/**
 * Exception thrown when pagination parameters are invalid.
 * This includes negative page numbers, invalid page sizes, etc.
 */
public class InvalidPaginationException extends RuntimeException {

    public InvalidPaginationException(String message) {
        super(message);
    }
    public InvalidPaginationException(String message, Throwable cause) {
        super(message, cause);
    }
}
