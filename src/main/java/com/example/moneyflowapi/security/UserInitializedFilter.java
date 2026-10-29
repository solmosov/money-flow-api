package com.example.moneyflowapi.security;

import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.handler.ErrorResponse;
import com.example.moneyflowapi.security.jwt.CustomUserPrincipal;
import com.example.moneyflowapi.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserInitializedFilter extends OncePerRequestFilter {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private final ObjectMapper objectMapper;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return SecurityConstants.INITIALIZATION_EXEMPT_URLS.stream()
                .anyMatch(pattern -> MATCHER.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal principal
                && !principal.isInitialized()) {

            ErrorCode errorCode = ErrorCode.USER_NOT_INITIALIZED;
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), ErrorResponse.builder()
                    .message(errorCode.getDefaultMessage())
                    .code(errorCode.getCode())
                    .build());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
