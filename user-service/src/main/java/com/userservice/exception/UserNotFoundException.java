package com.userservice.exception;

/**
 * Custom exception thrown when a requested user cannot be found in the system.
 * Extends RuntimeException to allow it to be thrown without explicit declaration.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
