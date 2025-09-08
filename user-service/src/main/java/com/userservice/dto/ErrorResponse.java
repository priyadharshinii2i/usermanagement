package com.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents a structured error response returned to the client
 * when an exception occurs in the application.
 *
 * This class provides details about the error such as:
 * - message: a short description of the error
 * - details: additional information about the error (e.g., API endpoint, cause)
 * - timestamp: the exact time when the error occurred
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private String details;
    private LocalDateTime timestamp;
}
