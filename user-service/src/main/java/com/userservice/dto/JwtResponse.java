package com.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JwtResponse
 *
 * A simple DTO (Data Transfer Object) used to send back the generated JWT token
 * as part of the login response.
 *
 * - Typically returned from authentication endpoints (e.g., /login).
 * - Contains only the JWT token string, which the client must store and
 *   include in the Authorization header ("Bearer <token>") for secured requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;
}
