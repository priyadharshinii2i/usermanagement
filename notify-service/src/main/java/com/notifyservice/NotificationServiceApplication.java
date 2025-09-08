package com.notifyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Notification Service Spring Boot application.
 *
 * The @SpringBootApplication annotation enables:
 *  - @Configuration: marks this as a source of bean definitions.
 *  - @EnableAutoConfiguration: enables Spring Boot’s auto-config mechanism.
 *  - @ComponentScan: scans the package for components, configurations, and services.
 */
@SpringBootApplication
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class);
    }
}
