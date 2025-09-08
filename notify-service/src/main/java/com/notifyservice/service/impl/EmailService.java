package com.notifyservice.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    /**
     * Sends email with sender tracking information in the body for MailHog visibility.
     */
    public void sendEmailWithSenderTracking(String to, String from, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setFrom(from);
        String enhancedBody = body + "\n\n--- Sender Tracking Information ---\n" +
                            "From: " + from + "\n" +
                            "To: " + to + "\n" +
                            "Sent via MailHog at: " + System.currentTimeMillis();
        message.setText(enhancedBody);
        mailSender.send(message);
    }
}
