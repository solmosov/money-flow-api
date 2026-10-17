package com.example.moneyflowapi.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DeleteAccountRequest(
        @NotBlank
        @Pattern(regexp = "\\d{6}")
        String code
) {
}
