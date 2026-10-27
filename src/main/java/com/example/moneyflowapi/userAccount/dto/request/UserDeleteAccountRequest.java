package com.example.moneyflowapi.userAccount.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Confirm permanent deletion of the user account")
public record UserDeleteAccountRequest(
        @Schema(description = "6-digit confirmation code", example = "482913")
        @NotBlank
        @Pattern(regexp = "\\d{6}")
        String code
) {
}