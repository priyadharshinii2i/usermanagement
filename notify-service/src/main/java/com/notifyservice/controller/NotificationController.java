package com.notifyservice.controller;

import com.notifyservice.constants.NotificationConstants;
import com.notifyservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * NotificationController is a REST controller responsible for handling
 * notification-related HTTP requests.
 * <p>
 * It exposes endpoints to send notifications (e.g., email) to users
 * by delegating the actual sending logic to the {@link NotificationService}.
 * </p>
 *
 * Base URL: {@code /notify}
 */
@RestController
@RequestMapping(NotificationConstants.BASE_PATH)
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Sends a notification to a specific user via email.
     * <p>
     * This endpoint accepts the recipient's email ID, subject, message, and optional sender email
     * as request parameters, then invokes the {@link NotificationService}
     * to deliver the notification with sender tracking.
     * </p>
     *
     * @param emailId the recipient's email address
     * @param subject the email subject
     * @param message the notification content/message body
     * @param senderEmail the sender's email address (optional, defaults to noreply@myapp.com)
     * @return {@link ResponseEntity} containing a success message
     */
    @PostMapping(NotificationConstants.SEND_NOTIFICATION_PATH)
    public ResponseEntity<String> send(@RequestParam(NotificationConstants.EMAIL_ID_PARAM) String emailId,
                                       @RequestParam(NotificationConstants.SUBJECT_PARAM) String subject,
                                       @RequestParam(NotificationConstants.MESSAGE_PARAM) String message,
                                       @RequestParam(value = "senderEmail", required = false, defaultValue = "priyadharshini.gunasekaran@ideas2it.com") String senderEmail) {
        logger.info("Received notification request from {} to {}", senderEmail, emailId);
        notificationService.sendNotification(emailId, subject, message, senderEmail);
        logger.info("Notification sent successfully from {} to {}", senderEmail, emailId);
        return ResponseEntity.ok(NotificationConstants.NOTIFICATION_SENT_SUCCESS);
    }
}