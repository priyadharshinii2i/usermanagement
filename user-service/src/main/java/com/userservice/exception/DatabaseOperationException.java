package com.userservice.exception;

/**
 * Exception thrown when database operations fail.
 * This includes save, update, delete, and other database-related failures.
 */
public class DatabaseOperationException extends RuntimeException {
    
    public DatabaseOperationException(String message) {
        super(message);
    }
    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
