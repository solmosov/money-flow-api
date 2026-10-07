package com.example.moneyflowapi.user;

import com.example.moneyflowapi.user.dto.response.AuthUserResponse;
import com.example.moneyflowapi.user.dto.response.BalanceResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserService extends UserDetailsService {

    BalanceResponse getBalance(UUID currentUserId);

    AuthUserResponse getMe();

    User registerUserByTelegram(long telegramUserId);
}
