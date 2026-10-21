package com.example.moneyflowapi.userAccount.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserDeleteAccountRequest(
        @NotBlank
        @Pattern(regexp = "\\d{6}")
        String code
) {
}
