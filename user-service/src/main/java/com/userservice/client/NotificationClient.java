package com.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for communicating with the Notification Service.
 * <p>
 * This client abstracts the REST API call to the notification service,
 * allowing other services to send notifications without manually handling
 * HTTP requests.
 */
@FeignClient(name = "notification-service", url = "http://localhost:9093")
public interface NotificationClient {

    /**
     * Sends a notification to the given email ID with the specified subject and message.
     * <p>
     * Internally calls the /notify/send endpoint of the notification service.
     *
     * @param emailId The recipient's email address.
     * @param subject The notification subject.
     * @param message The notification message to send.
     */
    @PostMapping("/notify/send")
    void sendNotification(@RequestParam String emailId, @RequestParam String subject, @RequestParam String message);
}
