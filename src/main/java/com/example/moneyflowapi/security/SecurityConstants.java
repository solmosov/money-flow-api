package com.example.moneyflowapi.security;

import java.util.List;

public final class SecurityConstants {

    public static final List<String> PUBLIC_URLS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/telegram",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/api/v1/exports/*/file",
            "/ws/**",
            "/actuator/health",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    );

    private SecurityConstants() {

    }
}
