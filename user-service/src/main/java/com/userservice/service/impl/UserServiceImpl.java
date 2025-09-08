package com.userservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.audit.Auditable;
import com.userservice.client.NotificationClient;
import com.userservice.dto.UserDto;
import com.userservice.dto.LoginUserDto;
import com.userservice.dto.RoleDto;
import com.userservice.dto.JwtResponse;
import com.userservice.util.JwtUtil;
import com.userservice.entity.User;
import com.userservice.entity.Role;
import com.userservice.exception.UserAlreadyExistException;
import com.userservice.exception.UserNotFoundException;
import com.userservice.exception.UserProfileNotExistException;
import com.userservice.exception.NotificationException;
import com.userservice.exception.DatabaseOperationException;
import com.userservice.repository.UserRepo;
import com.userservice.constants.ApplicationConstants;
import com.userservice.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the UserService interface.
 *
 * This class handles all user-related business logic such as
 * user creation, deletion, profile management, password changes,
 * and retrieving users. It also integrates with the NotificationClient
 * to send email notifications upon different actions.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepo userRepo;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final NotificationClient notificationClient;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepo userRepo, 
                          ObjectMapper objectMapper,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          NotificationClient notificationClient,
                          @Lazy JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.objectMapper = objectMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.notificationClient = notificationClient;
        this.jwtUtil = jwtUtil;
    }

    /**
     * {@inheritDoc}
     *
     * @throws UserAlreadyExistException if user with same email already exists
     * @throws NotificationException if notification service fails
     */
    public UserDto createUser(UserDto newUser) {
        logger.info("Creating user with email: {}", newUser.getEmailId());
        User existingUser = findUserByEmail(newUser.getEmailId());
        if (existingUser != null) {
            logger.warn("User already exists with email: {}", newUser.getEmailId());
            throw new UserAlreadyExistException(ApplicationConstants.USER_ALREADY_EXISTS + newUser.getEmailId());
        }
        User user = objectMapper.convertValue(newUser, User.class);
        user.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        user.setCreatedBy(user.getUsername());
        user = saveUser(user);

        logger.info("Sending welcome notification for new user: {}", newUser.getEmailId());
        sendWelcomeNotification(user);
        
        logger.info("User created successfully for email: {}", newUser.getEmailId());
        return objectMapper.convertValue(user, UserDto.class);
    }



    /**
     * Merges roles from new user DTO into existing user entity.
     *
     * @param existingUser the existing user entity
     * @param newUser the new user DTO with roles to merge
     */
    private void mergeUserRoles(User existingUser, UserDto newUser) {
        Set<Role> existingRoles = existingUser.getRoles();
        Set<Role> newRoles = newUser.getRoles().stream()
                .map(roleDto -> objectMapper.convertValue(roleDto, Role.class))
                .collect(Collectors.toSet());
        existingRoles.addAll(newRoles);
        existingUser.setRoles(existingRoles);
    }

    /**
     * Finds user by email with proper exception handling.
     * 
     * @param emailId the email to search for
     * @return User entity or null if not found
     * @throws DatabaseOperationException if database operation fails
     */
    private User findUserByEmail(String emailId) {
        try {
            return userRepo.findUserByEmailId(emailId);
        } catch (Exception e) {
            throw new DatabaseOperationException(ApplicationConstants.DB_FIND_USER_ERROR + e.getMessage(), e);
        }
    }

    /**
     * Saves user to database with proper exception handling.
     * 
     * @param user the user to save
     * @return saved user entity
     * @throws DatabaseOperationException if database operation fails
     */
    private User saveUser(User user) {
        try {
            return userRepo.save(user);
        } catch (Exception e) {
            throw new DatabaseOperationException(ApplicationConstants.DB_SAVE_USER_ERROR + e.getMessage(), e);
        }
    }


    /**
     * Sends welcome notification to user.
     * 
     * @param user the user to send notification to
     * @throws NotificationException if notification service fails
     */
    private void sendWelcomeNotification(User user) {
        try {
            notificationClient.sendNotification(user.getEmailId(),
                    ApplicationConstants.WELCOME_SUBJECT,
                    String.format(ApplicationConstants.WELCOME_MESSAGE, user.getUsername()));
        } catch (Exception e) {
            throw new NotificationException(ApplicationConstants.WELCOME_NOTIFICATION_ERROR + e.getMessage(), e);
        }
    }


    /**
     * {@inheritDoc}
     *
     * @throws UserNotFoundException if user not found
     * @throws NotificationException if notification service fails
     */
    @Transactional
    public boolean deleteUser(String emailId) {
        logger.info("Deleting user with email: {}", emailId);
        User user = findUserByEmail(emailId);
        if (user == null) {
            logger.warn("User not found for deletion: {}", emailId);
            throw new UserNotFoundException(ApplicationConstants.USER_NOT_FOUND + emailId);
        }

        deleteUserFromDatabase(user);
        logger.info("Sending deletion notification for user: {}", emailId);
        sendDeletionNotification(user);
        
        logger.info("User deleted successfully: {}", emailId);
        return true;
    }

    /**
     * Deletes user from database with proper exception handling.
     * 
     * @param user the user to delete
     * @throws DatabaseOperationException if database operation fails
     */
    private void deleteUserFromDatabase(User user) {
        try {
            userRepo.delete(user);
        } catch (Exception e) {
            throw new DatabaseOperationException(ApplicationConstants.DB_DELETE_USER_ERROR + e.getMessage(), e);
        }
    }

    /**
     * Sends deletion notification to user.
     * 
     * @param user the user to send notification to
     * @throws NotificationException if notification service fails
     */
    private void sendDeletionNotification(User user) {
        try {
            notificationClient.sendNotification(user.getEmailId(),
                    ApplicationConstants.USER_DELETED_SUBJECT,
                    String.format(ApplicationConstants.USER_DELETED_MESSAGE, user.getUsername()));
        } catch (Exception e) {
            throw new NotificationException(ApplicationConstants.DELETION_NOTIFICATION_ERROR + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @throws UserNotFoundException if no user exists with given email
     */
    public UserDto findByEmailId(String emailId) {
        logger.info("Finding user by email: {}", emailId);
        User user;
        try {
            user = userRepo.findUserByEmailId(emailId);
        } catch (Exception e) {
            logger.error("Database error while finding user: {}", emailId, e);
            throw new DatabaseOperationException(ApplicationConstants.DB_FIND_USER_ERROR + e.getMessage(), e);
        }
        if (user != null) {
            logger.info("User found successfully: {}", emailId);
            return objectMapper.convertValue(user, UserDto.class);
        } else {
            logger.warn("User not found: {}", emailId);
            throw new UserNotFoundException(ApplicationConstants.USER_NOT_FOUND + emailId);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @throws UserProfileNotExistException if no profile exists for given email
     */
    public UserDto viewUserProfile(String emailId) {
        logger.info("Viewing user profile for email: {}", emailId);
        User existingProfile = findUserByEmail(emailId);
        if (existingProfile != null) {
            logger.info("User profile found successfully: {}", emailId);
            return objectMapper.convertValue(existingProfile, UserDto.class);
        } else {
            logger.warn("User profile not found: {}", emailId);
            throw new UserProfileNotExistException(ApplicationConstants.USER_PROFILE_NOT_FOUND + emailId);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws UserNotFoundException if user not found
     * @throws NotificationException if notification service fails
     */
    public void changePassword(String password, String emailId) {
        logger.info("Changing password for user: {}", emailId);
        User user = findUserByEmail(emailId);
        if (user == null) {
            logger.warn("User not found for password change: {}", emailId);
            throw new UserNotFoundException(ApplicationConstants.USER_NOT_FOUND + emailId);
        }
        updateUserPassword(user, password);
        logger.info("Sending password change notification for user: {}", emailId);
        sendPasswordChangeNotification(user);
        logger.info("Password changed successfully for user: {}", emailId);
    }

    /**
     * Updates user password in database with proper exception handling.
     * 
     * @param user the user to update
     * @param password the new password
     * @throws DatabaseOperationException if database operation fails
     */
    @Transactional
    private void updateUserPassword(User user, String password) {
        user.setPassword(bCryptPasswordEncoder.encode(password));
        try {
            userRepo.save(user);
        } catch (Exception e) {
            throw new DatabaseOperationException(ApplicationConstants.DB_UPDATE_PASSWORD_ERROR + e.getMessage(), e);
        }
    }

    /**
     * Sends password change notification to user.
     * 
     * @param user the user to send notification to
     * @throws NotificationException if notification service fails
     */
    private void sendPasswordChangeNotification(User user) {
        try {
            notificationClient.sendNotification(user.getEmailId(),
                    ApplicationConstants.PASSWORD_CHANGED_SUBJECT,
                    String.format(ApplicationConstants.PASSWORD_CHANGED_MESSAGE, user.getUsername()));
        } catch (Exception e) {
            throw new NotificationException(ApplicationConstants.PASSWORD_NOTIFICATION_ERROR + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws UserNotFoundException if user not found
     */
    public JwtResponse login(LoginUserDto loginDto) {
        logger.info("Processing login for user: {}", loginDto.getEmailId());
        User user = findUserByEmail(loginDto.getEmailId());
        if (user == null || !bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            logger.warn("Login failed for user: {} - Invalid credentials", loginDto.getEmailId());
            return new JwtResponse(ApplicationConstants.INVALID_CREDENTIALS);
        }

        String token = JwtUtil.generateToken(user.getEmailId(), extractRoleNames(user.getRoles()));
        user.setToken(token);
        saveUser(user);
        
        logger.info("Login successful for user: {} - Token persisted to database", loginDto.getEmailId());
        return new JwtResponse(token);
    }

    /**
     * {@inheritDoc}
     *
     * @throws DatabaseOperationException if database operation fails
     */
    public Page<UserDto> findAllUsers(int page, int size) {
        logger.info("Finding all users - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        
        try {
            Page<User> users = userRepo.findAll(pageable);
            logger.info("Retrieved {} users from database", users.getTotalElements());
            return users.map(user -> objectMapper.convertValue(user, UserDto.class));
        } catch (Exception e) {
            logger.error("Database error while retrieving users", e);
            throw new DatabaseOperationException(ApplicationConstants.DB_RETRIEVE_USERS_ERROR + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws UserNotFoundException if user not found
     * @throws NotificationException if notification service fails
     */
    @Transactional
    public UserDto updateUser(String emailId, UserDto userDto) {
        logger.info("Updating user with email: {}", emailId);
        User existingUser = findUserByEmail(emailId);
        if (existingUser == null) {
            logger.warn("User not found for update: {}", emailId);
            throw new UserNotFoundException(ApplicationConstants.USER_NOT_FOUND + emailId);
        }
        updateUserDetails(existingUser, userDto);

        User updatedUser = saveUser(existingUser);
        logger.info("Sending update notification for user: {}", emailId);
        sendUpdateNotification(updatedUser);
        
        logger.info("User updated successfully: {}", emailId);
        return objectMapper.convertValue(updatedUser, UserDto.class);
    }

    /**
     * Updates user details from DTO, excluding sensitive fields (username and password).
     * 
     * @param existingUser the existing user entity
     * @param userDto the user DTO with updated details
     */
    private void updateUserDetails(User existingUser, UserDto userDto) {
        if (userDto.getPhoneNumber() != null && !userDto.getPhoneNumber().trim().isEmpty()) {
            existingUser.setPhoneNumber(userDto.getPhoneNumber().trim());
        }
        existingUser.setAge(userDto.getAge());
        existingUser.setCity(userDto.getCity().trim());
        Set<RoleDto> roles = userDto.getRoles();
        if (roles != null && !roles.isEmpty()) {
            mergeUserRoles(existingUser, userDto);
        }
    }

    /**
     * Validates if the stored token matches the provided token.
     * 
     * @param emailId the user's email
     * @param token the token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateStoredToken(String emailId, String token) {
        logger.info("Validating stored token for user: {}", emailId);
        User user = findUserByEmail(emailId);
        
        if (user == null) {
            logger.warn("User not found for token validation: {}", emailId);
            return false;
        }
        
        String storedToken = user.getToken();
        if (storedToken == null || storedToken.isEmpty()) {
            logger.warn("No stored token found for user: {}", emailId);
            return false;
        }
        
        boolean isValid = storedToken.equals(token);
        logger.info("Token validation result for user {}: {}", emailId, isValid);
        return isValid;
    }

    /**
     * Extracts role names from a set of roles for JWT token generation.
     * 
     * @param roles the set of roles
     * @return comma-separated string of role names
     */
    private String extractRoleNames(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.joining(","));
    }

    /**
     * Sends update notification to user.
     * 
     * @param user the user to send notification to
     * @throws NotificationException if notification service fails
     */
    private void sendUpdateNotification(User user) {
        try {
            notificationClient.sendNotification(user.getEmailId(),
                    ApplicationConstants.USER_UPDATED_SUBJECT,
                    String.format(ApplicationConstants.USER_UPDATED_MESSAGE, user.getUsername()));
        } catch (Exception e) {
            throw new NotificationException(ApplicationConstants.UPDATE_NOTIFICATION_ERROR + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws UserNotFoundException if user not found
     * @throws NotificationException if notification service fails
     */
    @Transactional
    public boolean clearUserToken(String emailId) {
        logger.info("Clearing token for user: {}", emailId);
        User user = findUserByEmail(emailId);
        
        if (user == null) {
            logger.warn("User not found for token clearing: {}", emailId);
            throw new UserNotFoundException(ApplicationConstants.USER_NOT_FOUND + emailId);
        }
        user.setToken(null);
        saveUser(user);
        logger.info("Sending logout notification for user: {}", emailId);
        sendLogoutNotification(user);
        logger.info("Token cleared successfully for user: {}", emailId);
        return true;
    }

    /**
     * Sends logout notification to user.
     * 
     * @param user the user to send notification to
     * @throws NotificationException if notification service fails
     */
    private void sendLogoutNotification(User user) {
        try {
            notificationClient.sendNotification(user.getEmailId(),
                    ApplicationConstants.USER_LOGOUT_SUBJECT,
                    String.format(ApplicationConstants.USER_LOGOUT_MESSAGE, user.getUsername()));
        } catch (Exception e) {
            throw new NotificationException(ApplicationConstants.LOGOUT_NOTIFICATION_ERROR + e.getMessage(), e);
        }
    }
}
