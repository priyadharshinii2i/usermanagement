package com.userservice.dto;

import com.userservice.entity.RoleType;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for user role information.
 * <p>
 * Used to transfer role data between application layers for user registration,
 * role assignment, and access control. Contains the role type (ADMIN or USER).
 * </p>
 * 
 * @see com.userservice.entity.RoleType
 */
@Data
@NoArgsConstructor
public class RoleDto {
    @NotBlank(message = "The role type should not be null")
    private RoleType name;
}
