package com.userservice.util;

import com.userservice.constants.ApplicationConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utility class for handling JWT (JSON Web Token) operations such as
 * token generation, extraction of claims, and validation.
 * <p>
 * Uses a shared secret key with HMAC-SHA for signing and verifying tokens.
 * Token is valid for 1 hour by default.
 */
@Component
public class JwtUtil {

    // Secret key used for signing and verifying JWT tokens
    private static final String SECRET = ApplicationConstants.JWT_SECRET;

    /**
     * Generates a JWT token containing the user's email ID and role as claims.
     *
     * @param emailId The email ID of the user (used as the subject).
     * @param role    The role of the user (added as a custom claim).
     * @return A signed JWT token as a String.
     */
    public static String generateToken(String emailId, String role) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(emailId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ApplicationConstants.JWT_EXPIRATION_TIME)) // 1 hour validity
                .signWith(key)
                .compact();
    }

    /**
     * Extracts the username (email ID) from the given JWT token.
     *
     * @param token The JWT token.
     * @return The subject (email ID) embedded in the token.
     */
    public String extractUsername(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extracts the role claim from the given JWT token.
     *
     * @param token The JWT token.
     * @return The role of the user as stored in the token.
     */
    public String extractRole(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    /**
     * Validates the given JWT token by verifying its signature and expiration.
     *
     * @param token The JWT token.
     * @return True if the token is valid, otherwise false.
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
