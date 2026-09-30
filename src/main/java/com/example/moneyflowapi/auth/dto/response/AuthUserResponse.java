package com.example.moneyflowapi.auth.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record AuthUserResponse(
        UUID id,
        String email,
        BigDecimal balance
) {
}
