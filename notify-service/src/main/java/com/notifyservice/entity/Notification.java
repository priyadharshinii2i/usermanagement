package com.notifyservice.entity;

import com.notifyservice.constants.NotificationConstants;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity class representing a notification record in the database.
 * Enhanced to track sender information for MailHog email monitoring.
 */
@Entity
@Table(name = NotificationConstants.NOTIFICATIONS_TABLE)
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String recipientEmail;
    private String senderEmail;       
    private String subject;
    private String message;
    private boolean sent;
    private LocalDateTime createdAt = LocalDateTime.now();
}
