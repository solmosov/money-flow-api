package com.example.moneyflowapi.account;

import java.util.UUID;

public interface AccountDeletionService {
    void requestCode(UUID userId);

    void confirm(UUID userId, String code);
}
