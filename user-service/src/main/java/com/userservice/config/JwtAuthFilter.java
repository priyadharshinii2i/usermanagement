package com.userservice.config;

import com.userservice.util.JwtUtil;
import com.userservice.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JwtAuthFilter
 *
 * Custom Spring Security filter that runs once per request and validates JWT tokens.
 * - Extracts the token from the Authorization header.
 * - Validates the token cryptographically (signature, expiration, structure).
 * - Extracts user details (email + role) from validated token.
 * - Validates token against database storage (revocation check).
 * - Populates Spring Security's Authentication context with the user's identity and authorities.
 *
 * This enables role-based access control using @PreAuthorize annotations in controllers.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtAuthFilter(JwtUtil jwtUtil, @Lazy UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /**
     * Intercepts every request to check for a valid JWT token.
     *
     * @param request  The incoming HTTP request
     * @param response The HTTP response
     * @param filterChain The filter chain to continue request processing
     * @throws ServletException if an error occurs during filtering
     * @throws IOException if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();
        logger.debug("Processing request: {} with auth header: {}",
                requestURI, authHeader != null ? "present" : "missing");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);
                if (userService.validateStoredToken(email, token)) {
                    logger.debug("Valid JWT token for user: {} with role: {}", email, role);
                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + role),
                            new SimpleGrantedAuthority(role)
                    );
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(email, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authentication set for user: {} with authorities: {}", email, authorities);
                } else {
                    logger.warn("Token not found in database for user: {}", email);
                }
            } else {
                logger.warn("Invalid JWT token provided for request: {}", requestURI);
            }
        } else {
            logger.debug("No Authorization header found for request: {}", requestURI);
        }
        filterChain.doFilter(request, response);
    }
}
