package com.notifyservice.exception;

/**
 * Exception thrown when database operations fail in the notification service.
 * This exception is used to indicate errors that occur during
 * database operations like save, find, update, or delete.
 */
public class DatabaseOperationException extends RuntimeException {

    public DatabaseOperationException(String message) {
        super(message);
    }

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
