package com.example.moneyflowapi.security;

import java.util.List;
import java.util.stream.Stream;

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

    public static final List<String> INITIALIZATION_EXEMPT_URLS = Stream.concat(
            PUBLIC_URLS.stream(),
            Stream.of("/api/v1/user-account/start")
    ).toList();

    private SecurityConstants() {

    }
}
