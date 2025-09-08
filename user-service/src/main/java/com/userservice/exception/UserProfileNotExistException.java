package com.userservice.exception;

/**
 * Custom exception thrown when a user profile does not exist in the system.
 * Extends RuntimeException so it can be thrown without mandatory declaration in method signatures.
 */
public class UserProfileNotExistException extends RuntimeException {

    public UserProfileNotExistException(String message) {
        super(message);
    }
}
