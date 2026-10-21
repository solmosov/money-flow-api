package com.example.moneyflowapi.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Login with Telegram Mini App")
public record AuthenticationByTelegramRequest(
        @Schema(description = "Raw initData string provided by Telegram WebApp", example = "query_id=AAH...&user=%7B%22id%22...&auth_date=1729500000&hash=abc123")
        @NotBlank
        @Size(max = 4096)
        @JsonProperty("init_data")
        String initData
) {
}