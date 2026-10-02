package com.example.moneyflowapi.security.jwt.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GenerateAccessTokenDto(
        UUID userId,
        UUID customerId,
        String userEmail
) {
}
