package com.notifyservice.service;

/**
 * Service interface for managing notifications.
 * 
 * This interface defines the contract for notification-related operations
 * such as sending notifications and managing notification records.
 */
public interface NotificationService {

    /**
     * Sends a notification to the given email ID.
     *
     * @param emailId recipient email address
     * @param subject email subject
     * @param message notification content
     * @param senderEmail sender email address (optional, defaults to noreply@myapp.com)
     * @throws com.notifyservice.exception.DatabaseOperationException if database operation fails
     */
    void sendNotification(String emailId, String subject, String message, String senderEmail);
}

