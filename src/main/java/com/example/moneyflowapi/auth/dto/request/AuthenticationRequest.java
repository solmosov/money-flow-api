package com.example.moneyflowapi.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Login with email and password")
public record AuthenticationRequest(
        @Schema(description = "User email", example = "user@example.com")
        @NotBlank
        @Email
        @Size(max = 254)
        String email,

        @Schema(description = "User password", example = "StrongPass123", format = "password")
        @NotBlank
        @Size(min = 8, max = 128)
        String password
) {
}