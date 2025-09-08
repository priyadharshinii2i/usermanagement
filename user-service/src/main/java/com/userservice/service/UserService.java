package com.userservice.service;

import com.userservice.dto.UserDto;
import com.userservice.dto.LoginUserDto;
import com.userservice.dto.JwtResponse;
import org.springframework.data.domain.Page;

/**
 * Service interface for managing Users and their Profiles.
 * Defines the contract for user-related operations such as
 * creating, deleting, finding, and updating users, as well as
 * handling user profiles.
 */
public interface UserService {

    /**
     * Creates a new user in the system.
     *
     * @param userDto the user details to create
     * @return the created user as a DTO
     */
    UserDto createUser(UserDto userDto);

    /**
     * Deletes a user by their email ID.
     *
     * @param emailId the email ID of the user to delete
     * @return true if the user was deleted successfully
     */
    boolean deleteUser(String emailId);

    /**
     * Finds a user by their email ID.
     *
     * @param emailId the email ID of the user
     * @return the user details as a DTO
     */
    UserDto findByEmailId(String emailId);

    /**
     * Retrieves a user's profile by their email ID.
     *
     * @param emailId the email ID of the user
     * @return the user profile as a DTO
     */
    UserDto viewUserProfile(String emailId);

    /**
     * Changes the password of a user.
     *
     * @param password the new password to set
     * @param emailId the email ID of the user
     */
    void changePassword(String password, String emailId);

    /**
     * Authenticates a user and generates a JWT token upon successful login.
     *
     * @param loginDto LoginUserDto containing email and password
     * @return JwtResponse containing JWT token or error message
     */
    JwtResponse login(LoginUserDto loginDto);

    /**
     * Retrieves all users in the system.
     *
     * @return a set of user DTOs
     */
    Page<UserDto> findAllUsers(int page, int size);

    /**
     * Updates user details by email ID.
     *
     * @param emailId the email ID of the user to update
     * @param userDto the updated user details
     * @return the updated user as a DTO
     */
    UserDto updateUser(String emailId, UserDto userDto);

    /**
     * Validates if the stored token matches the provided token.
     *
     * @param emailId the user's email
     * @param token the token to validate
     * @return true if token is valid, false otherwise
     */
    boolean validateStoredToken(String emailId, String token);

    /**
     * Clears the stored token for a user (logout functionality).
     *
     * @param emailId the user's email
     * @return true if token was cleared successfully, false otherwise
     */
    boolean clearUserToken(String emailId);
}