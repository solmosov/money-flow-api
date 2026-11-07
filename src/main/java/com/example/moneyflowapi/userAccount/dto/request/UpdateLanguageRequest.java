package com.example.moneyflowapi.userAccount.dto.request;

import com.example.moneyflowapi.user.UserLang;
import jakarta.validation.constraints.NotNull;

public record UpdateLanguageRequest(
        @NotNull
        UserLang lang
) {
}