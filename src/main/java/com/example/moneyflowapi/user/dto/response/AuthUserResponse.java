package com.example.moneyflowapi.user.dto.response;

import com.example.moneyflowapi.currency.Currency;
import com.example.moneyflowapi.role.RoleName;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record AuthUserResponse(
        UUID id,
        String email,
        List<RoleName> roles
) {
}
