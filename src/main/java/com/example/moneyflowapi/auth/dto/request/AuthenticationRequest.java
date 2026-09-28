package com.example.moneyflowapi.auth.dto.request;

public record AuthenticationRequest(
        String email,
        String password
) {
}
