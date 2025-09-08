package com.userservice.constants;

/**
 * Application-level constants for the User Management Service.
 * 
 * This class contains all constant values used throughout the application
 * including error messages, notification messages, and validation rules.
 */
public final class ApplicationConstants {


    private ApplicationConstants() {
    }

    public static final String DB_FIND_USER_ERROR = "Failed to find user by email in database: ";
    public static final String DB_SAVE_USER_ERROR = "Failed to save user to database: ";
    public static final String DB_DELETE_USER_ERROR = "Failed to delete user from database: ";
    public static final String DB_UPDATE_PASSWORD_ERROR = "Failed to update user password in database: ";
    public static final String DB_RETRIEVE_USERS_ERROR = "Failed to retrieve users from database: ";
    public static final String WELCOME_SUBJECT = "Welcome to User Management System";
    public static final String USER_DELETED_SUBJECT = "Account Deletion Notification";
    public static final String USER_UPDATED_SUBJECT = "Profile Update Notification";
    public static final String USER_LOGOUT_SUBJECT = "Logout Notification";
    public static final String PASSWORD_CHANGED_SUBJECT = "Password Change Notification";
    public static final String WELCOME_MESSAGE = "Welcome %s! Your account has been created.";
    public static final String USER_DELETED_MESSAGE = "User %s deleted.";
    public static final String USER_UPDATED_MESSAGE = "User %s details have been updated successfully.";
    public static final String USER_LOGOUT_MESSAGE = "User %s has logged out successfully.";
    public static final String PASSWORD_CHANGED_MESSAGE = "User %s has changed the password.";
    public static final String WELCOME_NOTIFICATION_ERROR = "Failed to send welcome notification: ";
    public static final String DELETION_NOTIFICATION_ERROR = "Failed to send deletion notification: ";
    public static final String UPDATE_NOTIFICATION_ERROR = "Failed to send update notification: ";
    public static final String LOGOUT_NOTIFICATION_ERROR = "Failed to send logout notification: ";
    public static final String PASSWORD_NOTIFICATION_ERROR = "Failed to send password change notification: ";
    public static final String USER_NOT_FOUND = "User not found with email: ";
    public static final String USER_ALREADY_EXISTS = "User already exists with email: ";
    public static final String USER_PROFILE_NOT_FOUND = "UserProfile not found Exception with email: ";
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String JWT_SECRET = "hD7$kL9!qP4vX2sFzR8mB1@tY6wG3nJ0";
    public static final long JWT_EXPIRATION_TIME = 3600_000;

}
