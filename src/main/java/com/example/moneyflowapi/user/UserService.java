package com.example.moneyflowapi.user;

import com.example.moneyflowapi.user.dto.response.AuthUserResponse;
import com.example.moneyflowapi.user.dto.response.BalanceResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserService extends UserDetailsService {

    UserDetails loadUserByTelegramUserId(Long telegramUserId);

    BalanceResponse getBalance(UUID currentUserId);

    AuthUserResponse getMe();

    User registerUserByTelegram(long telegramUserId);

    void deleteAccount(UUID userId);
}
