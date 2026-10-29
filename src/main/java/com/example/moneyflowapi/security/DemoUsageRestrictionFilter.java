package com.example.moneyflowapi.security;

import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.security.jwt.CustomUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class DemoUsageRestrictionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public DemoUsageRestrictionFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        return SecurityConstants.PUBLIC_URLS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.getPrincipal() instanceof CustomUserPrincipal principal
                && principal.isDemoUsage()
                && !isAllowed(request)) {

            ErrorCode errorCode = ErrorCode.USER_IS_DEMO_USAGE;

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "status", errorCode.getCode(),
                    "message", errorCode.getDefaultMessage()
            ));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAllowed(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        return SecurityConstants.DEMO_USAGE_ALLOWED_ENDPOINTS.stream()
                .anyMatch(endpoint -> endpoint.method().name().equalsIgnoreCase(method)
                        && pathMatcher.match(endpoint.pattern(), path));
    }
}
