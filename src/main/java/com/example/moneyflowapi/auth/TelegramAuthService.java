package com.example.moneyflowapi.auth;

public interface TelegramAuthService {
    String verify(String initData) throws Exception;
}
