package com.userservice.repository;

import com.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing User entities.
 * Extends JpaRepository to provide built-in CRUD operations and pagination support.
 */
@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    /**
     * Custom query method to find a user by email ID.
     * Spring Data JPA will derive the SQL query automatically from the method name.
     *
     * @param emailId the email ID of the user to search for
     * @return the User entity if found, otherwise null
     */
    User findUserByEmailId(String emailId);
}
