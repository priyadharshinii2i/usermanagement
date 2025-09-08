package com.userservice.exception;

/**
 * Exception thrown when there are issues with the notification service.
 * This could be due to network issues, service unavailability, or invalid notification data.
 */
public class NotificationException extends RuntimeException {
    
    public NotificationException(String message) {
        super(message);
    }
    
    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
