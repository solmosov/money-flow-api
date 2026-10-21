package com.example.moneyflowapi.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to update an account")
public record UpdateAccountRequest(
        @Schema(description = "New account name", example = "Savings card", maxLength = 50)
        @NotBlank
        @Size(max = 50)
        String name
) {
}