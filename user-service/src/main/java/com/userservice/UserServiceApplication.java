package com.userservice;

import com.userservice.service.impl.AuditorAwareImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Entry point for the UserService Spring Boot application.
 *
 * - @SpringBootApplication: Marks this class as the bootstrap class,
 *   enabling component scanning, auto-configuration, and Spring Boot setup.
 *
 * - @EnableFeignClients: Enables Feign client support, allowing this service
 *   to communicate with other microservices via declarative REST clients.
 */
@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
public class UserServiceApplication {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
    /**
     * Main method to launch the Spring Boot application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
