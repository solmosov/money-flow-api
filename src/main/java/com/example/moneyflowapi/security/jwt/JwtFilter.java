package com.example.moneyflowapi.security.jwt;

import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.user.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {

            final Claims claims = jwtService.parseToken(jwt);

            if (!jwtService.isTokenValid(jwt)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                // JWT claims
                final UUID userId = UUID.fromString(claims.getSubject());
                final String email = claims.get(JwtService.USER_EMAIL, String.class);
                final Long telegramUserId = claims.get(JwtService.TELEGRAM_USER_ID, Long.class);
                final String customerIdValue = claims.get(JwtService.CUSTOMER_ID, String.class);
                final UUID customerId = customerIdValue != null
                        ? UUID.fromString(customerIdValue)
                        : null;

                if(email == null && telegramUserId == null){
                    filterChain.doFilter(request, response);
                    return;
                }


                UserDetails userDetails;

                if(email != null){
                    userDetails = userDetailsService.loadUserByUsername(email);
                }else {
                    userDetails = userService.loadUserByTelegramUserId(telegramUserId);
                }

                final boolean initialized = userDetails instanceof User user
                        && user.getInitializedAt() != null;

                final boolean demoUsage = userDetails instanceof User user
                        && Boolean.TRUE.equals(user.getIsDemoUsage());

                final CustomUserPrincipal principal = CustomUserPrincipal.builder()
                        .userId(userId)
                        .email(email)
                        .customerId(customerId)
                        .authorities(userDetails.getAuthorities())
                        .initialized(initialized)
                        .demoUsage(demoUsage)
                        .build();

                final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            }
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
