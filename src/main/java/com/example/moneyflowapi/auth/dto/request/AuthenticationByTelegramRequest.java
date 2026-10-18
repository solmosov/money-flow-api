package com.example.moneyflowapi.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationByTelegramRequest(
        @NotBlank
        @Size(max = 4096)
        String initData
) {
}
