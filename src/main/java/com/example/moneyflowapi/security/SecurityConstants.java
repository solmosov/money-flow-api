package com.example.moneyflowapi.security;

import java.util.List;

public final class SecurityConstants {

    public static final List<String> PUBLIC_URLS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/mock",
            "/api/v1/auth/telegram",
            "/api/v2/auth/register",
            "/api/v2/auth/refresh",
            "/ws/**",
            "/actuator/health"
    );

    private SecurityConstants() {

    }
}
