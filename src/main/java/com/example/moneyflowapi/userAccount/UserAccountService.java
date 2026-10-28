package com.example.moneyflowapi.userAccount;

import com.example.moneyflowapi.userAccount.dto.request.UserStartRequest;

import java.util.UUID;

public interface UserAccountService {
    void initialize(UUID userId, String language, UserStartRequest request);

}
