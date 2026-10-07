package com.example.moneyflowapi.auth;

import com.example.moneyflowapi.auth.dto.response.AuthenticationResponse;

public interface TelegramAuthService {
    AuthenticationResponse verify(String initData) throws Exception;
}
