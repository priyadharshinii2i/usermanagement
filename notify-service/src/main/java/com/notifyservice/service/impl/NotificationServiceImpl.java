package com.notifyservice.service.impl;

import com.notifyservice.entity.Notification;
import com.notifyservice.repo.NotificationRepo;
import com.notifyservice.service.NotificationService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;

/**
 * Service class for handling notifications.
 * This class is responsible for sending emails and saving notification details in the database.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final EmailService emailService;
    private final NotificationRepo notificationRepo;

    public NotificationServiceImpl(EmailService emailService, NotificationRepo notificationRepo) {
        this.emailService = emailService;
        this.notificationRepo = notificationRepo;
    }

    /**
     * Sends a notification to the given email address.
     * @param emailId recipient email address
     * @param subject subject of the notification
     * @param message content of the notification
     * @param senderEmail sender email address
     */
    public void sendNotification(String emailId, String subject, String message, String senderEmail) {
        logger.info("Sending notification from {} to {}", senderEmail, emailId);
        try {
            emailService.sendEmailWithSenderTracking(emailId, senderEmail, subject, message);
            Notification notification = new Notification();
            notification.setRecipientEmail(emailId);
            notification.setSenderEmail(senderEmail);
            notification.setSubject(subject);
            notification.setMessage(message);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setSent(true);
            notificationRepo.save(notification);
            logger.info("Notification sent successfully from {} to {}", senderEmail, emailId);
        } catch (Exception e) {
            logger.error("Failed to send notification from {} to {}", senderEmail, emailId, e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }
}
