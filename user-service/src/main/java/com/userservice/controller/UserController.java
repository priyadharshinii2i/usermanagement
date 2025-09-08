package com.userservice.controller;

import com.userservice.dto.JwtResponse;
import com.userservice.dto.UserDto;
import com.userservice.dto.LoginUserDto;
import com.userservice.service.UserService;
import com.userservice.exception.InvalidPaginationException;
import com.userservice.service.impl.RedisLoggingService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UserController
 *
 * REST controller that handles user-related operations such as registration,
 * login, profile creation, password changes, user retrieval, and deletion.
 * It uses JWT for authentication and role-based authorization for secure access.
 *
 * Endpoints are protected with Spring Security annotations:
 * - ROLE_USER: For standard user operations (profile management, password change, viewing profile)
 * - ROLE_ADMIN: For admin operations (retrieving all users, deleting users)
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final RedisLoggingService redisLoggingService;

    public UserController(UserService userService, RedisLoggingService redisLoggingService) {
        this.userService = userService;
        this.redisLoggingService = redisLoggingService;
    }

    /**
     * Registers a new user with provided details.
     *
     * @param newUser CreateUserDto containing email, password, and role.
     * @return ResponseEntity with success message.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserDto newUser) {
        logger.info("User registration request received for email: {}", newUser.getEmailId());
        userService.createUser(newUser);
        logger.info("User registered successfully for email: {}", newUser.getEmailId());
        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }

    /**
     * Authenticates a user and generates a JWT token upon successful login.
     *
     * @param loginDto LoginUserDto containing email and password.
     * @return ResponseEntity containing JWT token or unauthorized message.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginUserDto loginDto) {
        logger.info("Login attempt for email: {}", loginDto.getEmailId());
        JwtResponse response = userService.login(loginDto);
        if (response.getToken().equals("Invalid credentials")) {
            logger.warn("Login failed for email: {} - Invalid credentials", loginDto.getEmailId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        logger.info("Login successful for email: {}", loginDto.getEmailId());
        return ResponseEntity.ok(response);
    }

    /**
     * Allows a user to change their password.
     * Accessible only by users with ROLE_USER.
     *
     * @param password New password to set.
     * @param emailId Email ID of the user.
     * @return ResponseEntity with success message.
     */
    @PatchMapping("/forgotPassword")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @RateLimiter(name = "getMessageRateLimit", fallbackMethod = "getMessageFallBack")
    public ResponseEntity<String> forgotPassword(@RequestBody String password, @RequestParam String emailId) {
        logger.info("Password change request received for email: {}", emailId);
        userService.changePassword(password, emailId);
        logger.info("Password changed successfully for email: {}", emailId);
        return new ResponseEntity<>("Updated user password", HttpStatus.OK);
    }

    public ResponseEntity<String> getMessageFallBack(String password, String emailId, RequestNotPermitted exception) {

        logger.info("Rate limit has applied, So no further calls are getting accepted " +emailId);
        redisLoggingService.logRateLimit(emailId);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Too many requests : No further request will be accepted. Please try after sometime");
    }

    /**
     * Retrieves a user by their email ID.
     * Accessible only by admins with ROLE_ADMIN.
     *
     * @param emailId Email ID of the user.
     * @return ResponseEntity containing user details.
     */
    @GetMapping("/getUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserByEmailId(@RequestParam String emailId) {
        logger.info("Get user request received for email: {}", emailId);
        UserDto retrievedUser = userService.findByEmailId(emailId);
        logger.info("User retrieved successfully for email: {}", emailId);
        return new ResponseEntity<>(retrievedUser, HttpStatus.OK);
    }

    /**
     * Retrieves all users in the system.
     * Accessible only by admins with ROLE_ADMIN.
     *
     * @return ResponseEntity containing a set of users.
     */
    @GetMapping("/getAllUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        logger.info("Get all users request received - page: {}, size: {}", page, size);
        validatePaginationParameters(page, size);
        
        Page<UserDto> users = userService.findAllUsers(page, size);
        logger.info("Retrieved {} users successfully", users.getTotalElements());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Validates pagination parameters to ensure they are within acceptable ranges.
     * 
     * @param page the page number (0-based)
     * @param size the page size
     * @throws InvalidPaginationException if parameters are invalid
     */
    private void validatePaginationParameters(int page, int size) {
        if (page < 0 || size <= 0 || size > 100) {
            String errorMessage = page < 0 ? "Page number must be non-negative: " + page :
                                size <= 0 ? "Page size must be positive: " + size :
                                "Page size cannot exceed 100: " + size;
            throw new InvalidPaginationException(errorMessage);
        }
    }

    /**
     * Allows a user to view their profile.
     * Accessible only by users with ROLE_USER.
     *
     * @param emailId Email ID of the user.
     * @return ResponseEntity containing user profile details.
     */
    @GetMapping("/viewProfile")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserDto> viewUserProfile(@RequestParam String emailId) {
        logger.info("View profile request received for email: {}", emailId);
        UserDto retrievedUserProfile = userService.viewUserProfile(emailId);
        logger.info("User profile viewed successfully for email: {}", emailId);
        return new ResponseEntity<>(retrievedUserProfile, HttpStatus.OK);
    }

    /**
     * Deletes a user by their email ID.
     * Accessible only by admins with ROLE_ADMIN.
     *
     * @param emailId Email ID of the user.
     * @return ResponseEntity with success message.
     */
    @DeleteMapping("/removeUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUserById(@RequestParam String emailId) {
        logger.info("Delete user request received for email: {}", emailId);
        userService.deleteUser(emailId);
        logger.info("User deleted successfully for email: {}", emailId);
        return new ResponseEntity<>("User deleted", HttpStatus.OK);
    }

    /**
     * Updates user details by email ID.
     * Accessible only by users with ROLE_USER.
     *
     * @param emailId Email ID of the user to update.
     * @param userDto Updated user details.
     * @return ResponseEntity containing updated user details.
     */
    @PutMapping("/updateUser")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserDto> updateUser(@RequestParam String emailId, @Valid @RequestBody UserDto userDto) {
        logger.info("Update user request received for email: {}", emailId);
        UserDto updatedUser = userService.updateUser(emailId, userDto);
        logger.info("User updated successfully for email: {}", emailId);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * Validates a stored token for a user.
     * Accessible only by authenticated users.
     *
     * @param emailId Email ID of the user.
     * @param token Token to validate.
     * @return ResponseEntity with validation result.
     */
    @PostMapping("/validateToken")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> validateToken(@RequestParam String emailId, @RequestParam String token) {
        logger.info("Token validation request received for email: {}", emailId);
        boolean isValid = userService.validateStoredToken(emailId, token);
        
        // Ternary operator for cleaner response handling
        String message = isValid ? "Token is valid" : "Token is invalid";
        HttpStatus status = isValid ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        
        if (isValid) {
            logger.info("Token validated successfully for email: {}", emailId);
        } else {
            logger.warn("Token validation failed for email: {}", emailId);
        }
        
        return new ResponseEntity<>(message, status);
    }

    /**
     * Logs out a user by clearing their stored token.
     * Accessible only by authenticated users.
     *
     * @param emailId Email ID of the user to logout.
     * @return ResponseEntity with success message.
     */
    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> logout(@RequestParam String emailId) {
        logger.info("Logout request received for email: {}", emailId);
        userService.clearUserToken(emailId);
        logger.info("User logged out successfully: {}", emailId);
        return new ResponseEntity<>("User logged out successfully", HttpStatus.OK);
    }

}
