package com.example.moneyflowapi.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
public class CustomUserPrincipal implements UserDetails {

    private final UUID userId;
    private final UUID realUserId;
    private final UUID customerId;
    private final String email;
    private final boolean initialized;
    private final boolean demoUsage;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userId.toString();
    }
}
