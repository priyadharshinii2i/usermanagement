package com.notifyservice.exception;

/**
 * Exception thrown when notification operations fail.
 * This exception is used to indicate errors that occur during
 * notification sending, validation, or database operations.
 */
public class NotificationException extends RuntimeException {

    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
