package com.example.moneyflowapi.user;

import java.util.Locale;

public enum UserLang {
    EN,
    UZ,
    RU;

    public static UserLang fromHeader(String header) {
        if (header == null || header.isBlank()) {
            return UZ;
        }

        String primary = header.split(",")[0].split(";")[0].trim();
        String code = primary.split("[-_]")[0].toUpperCase(Locale.ROOT);

        for (UserLang lang : values()) {
            if (lang.name().equals(code)) {
                return lang;
            }
        }

        return UZ;
    }
}
