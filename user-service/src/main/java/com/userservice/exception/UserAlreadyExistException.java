package com.userservice.exception;

/**
 * Custom exception thrown when a user already exists in the system.
 * Extends RuntimeException so it can be thrown without being declared in method signatures.
 */
public class UserAlreadyExistException extends RuntimeException {

    public UserAlreadyExistException(String message) {
        super(message);
    }
}
