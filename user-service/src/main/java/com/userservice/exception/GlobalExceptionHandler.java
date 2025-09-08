package com.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.userservice.dto.ErrorResponse;

import java.time.LocalDateTime;

/**
 * GlobalExceptionHandler handles all custom exceptions in the application.
 * It uses @ControllerAdvice to provide centralized exception handling across all controllers.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles UserAlreadyExistException.
     * Returns a CONFLICT (409) status with error details.
     */
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistException ex,
            WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Handles UserNotFoundException.
     * Returns a NOT_FOUND (404) status with error details.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception, WebRequest request){
        ErrorResponse error = new ErrorResponse(
                exception.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles UserProfileNotExistException.
     * Returns a NOT_FOUND (404) status with error details.
     */
    @ExceptionHandler(UserProfileNotExistException.class)
    public ResponseEntity<ErrorResponse> handleUserProfileNotExistException(UserProfileNotExistException exception, WebRequest request){
        ErrorResponse error = new ErrorResponse(
                exception.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles NotificationException.
     * Returns a SERVICE_UNAVAILABLE (503) status with error details.
     */
    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ErrorResponse> handleNotificationException(NotificationException exception, WebRequest request){
        ErrorResponse error = new ErrorResponse(
                exception.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handles InvalidPaginationException.
     * Returns a BAD_REQUEST (400) status with error details.
     */
    @ExceptionHandler(InvalidPaginationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPaginationException(InvalidPaginationException exception, WebRequest request){
        ErrorResponse error = new ErrorResponse(
                exception.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles DatabaseOperationException.
     * Returns a INTERNAL_SERVER_ERROR (500) status with error details.
     */
    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseOperationException(DatabaseOperationException exception, WebRequest request){
        ErrorResponse error = new ErrorResponse(
                exception.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
