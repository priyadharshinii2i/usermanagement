package com.userservice.service.impl;

import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * This class tells spring get authenticated user detail from security context.
 */
@EnableJpaAuditing
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * This method tells Spring Data JPA who the current user from SecurityContext
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            return Optional.of(authentication.getName());
        }
        return Optional.of("system");
    }
}