package com.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) used for handling user login requests.
 * <p>
 * Carries the login credentials (email and password) from the client
 * to the authentication endpoint.
 */
@Data
@NoArgsConstructor
public class LoginUserDto {
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please provide a valid email address")
    private String emailId;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must have at least 8 characters")
    private String password;
}
