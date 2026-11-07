package com.example.moneyflowapi.security;

import org.springframework.http.HttpMethod;

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
            "/v3/api-docs/**",
            "/webhook/telegram/**"
    );

    public static final List<String> INITIALIZATION_EXEMPT_URLS = Stream.concat(
            PUBLIC_URLS.stream(),
            Stream.of(
                    "/api/v1/user-account/start",
                    "/api/v1/currencies",
                    "/api/v1/currencies/*"
            )
    ).toList();

    public record AllowedEndpoint(HttpMethod method, String pattern) {
    }

    public static final List<AllowedEndpoint> DEMO_USAGE_ALLOWED_ENDPOINTS = List.of(
            new AllowedEndpoint(HttpMethod.GET, "/api/v1/currencies"),
            new AllowedEndpoint(HttpMethod.GET, "/api/v1/currencies/*"),
            new AllowedEndpoint(HttpMethod.GET, "/api/v1/categories"),
            new AllowedEndpoint(HttpMethod.GET, "/api/v1/categories/*"),
            new AllowedEndpoint(HttpMethod.GET, "/api/v1/wallets"),
            new AllowedEndpoint(HttpMethod.GET, "/api/v1/wallets/*"),
            new AllowedEndpoint(HttpMethod.GET, "/api/v1/transactions"),
            new AllowedEndpoint(HttpMethod.GET, "/api/v1/transactions/**"),
            new AllowedEndpoint(HttpMethod.GET, "/api/v1/goals"),
            new AllowedEndpoint(HttpMethod.GET, "/api/v1/goals/**"),
            new AllowedEndpoint(HttpMethod.GET, "/api/v1/users/me"),
            new AllowedEndpoint(HttpMethod.GET, "/api/v1/users/me/**"),
            new AllowedEndpoint(HttpMethod.GET, "/api/v1/user-account/**"),
            new AllowedEndpoint(HttpMethod.POST, "/api/v1/user-account/start"),
            new AllowedEndpoint(HttpMethod.POST, "/api/v1/user-account/deletion-code"),
            new AllowedEndpoint(HttpMethod.DELETE, "/api/v1/user-account"),
            new AllowedEndpoint(HttpMethod.DELETE, "/api/v1/user-account/demo-data")
    );

    private SecurityConstants() {

    }
}
