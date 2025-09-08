package com.notifyservice.constants;

/**
 * Constants related to notification operations and API endpoints.
 * 
 * This class contains all the constant values used throughout the notification service
 * including API paths, response messages, and notification status values.
 */
public final class NotificationConstants {

    private NotificationConstants() {
    }
    public static final String BASE_PATH = "/notify";
    public static final String SEND_NOTIFICATION_PATH = "/send";
    public static final String EMAIL_ID_PARAM = "emailId";
    public static final String SUBJECT_PARAM = "subject";
    public static final String MESSAGE_PARAM = "message";
    public static final String NOTIFICATION_SENT_SUCCESS = "Notification sent successfully";
    public static final String NOTIFICATIONS_TABLE = "notifications";
}
