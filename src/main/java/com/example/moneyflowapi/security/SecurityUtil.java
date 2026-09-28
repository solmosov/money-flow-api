package com.example.moneyflowapi.security;

import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.security.jwt.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtil {

    private SecurityUtil() {

    }

    public static String getCurrentUserEmail() {
        CustomUserPrincipal userPrincipal = getPrincipal();
        return userPrincipal.getEmail();
    }


    public static UUID getCurrentUserId() {
        CustomUserPrincipal userPrincipal = getPrincipal();
        return userPrincipal.getUserId();
    }

    public static UUID getCurrentUserCustomerId() {
        CustomUserPrincipal userPrincipal = getPrincipal();
        return userPrincipal.getCustomerId();
    }

    private static CustomUserPrincipal getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserPrincipal customUserPrincipal) {
            return customUserPrincipal;
        }

        throw new BusinessException(ErrorCode.UNAUTHORIZED);
    }

}
