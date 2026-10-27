package com.example.moneyflowapi.userAccount;

import java.util.UUID;

public interface UserAccountDeletionService {
    void requestCode(UUID userId);

    void confirm(UUID userId, String code);
}
