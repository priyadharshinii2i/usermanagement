package com.userservice.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UserDto {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3–20 characters")
    private String username;
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please provide a valid email address")
    private String emailId;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    @NotBlank(message = "FirstName cannot be blank")
    @Size(min = 3, max = 20, message = "FirstName must be between 3–20 characters")
    private String firstName;
    @NotBlank(message = "SecondName cannot be blank")
    @Size(min = 3, max = 20, message = "SecondName must be between 3–20 characters")
    private String lastName;
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must be less than or equal to 100")
    private int age;
    @NotBlank(message = "City cannot be blank")
    private String city;
    @NotEmpty(message = "User must have at least one role")
    private Set<RoleDto> roles = new HashSet<>();
}