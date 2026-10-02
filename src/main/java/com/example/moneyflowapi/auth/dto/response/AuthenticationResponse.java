package com.example.moneyflowapi.auth.dto.response;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String accessToken,
        String tokenType
) {
}
