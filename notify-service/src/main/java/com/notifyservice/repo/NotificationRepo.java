package com.notifyservice.repo;

import com.notifyservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on Notification entities.
 * Extends JpaRepository to provide built-in methods like save, findAll, findById, delete, etc.
 */
@Repository
public interface NotificationRepo extends JpaRepository<Notification, Integer> {}
