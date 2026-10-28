package com.example.moneyflowapi.wallet.resolver;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class MainWalletNameResolver {

    private static final String DEFAULT_NAME = "Main Wallet";

    private static final Map<String, String> NAMES = Map.of(
            "en", "Main Wallet",
            "uz", "Asosiy hamyon",
            "ru", "Основной кошелёк"
    );

    public String resolve(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.isBlank()) {
            return DEFAULT_NAME;
        }
        try {
            List<Locale.LanguageRange> ranges = Locale.LanguageRange.parse(acceptLanguage);
            for (Locale.LanguageRange range : ranges) {
                String language = range.getRange().split("-")[0].toLowerCase();
                String name = NAMES.get(language);
                if (name != null) {
                    return name;
                }
            }
        } catch (IllegalArgumentException ignored) {
        }
        return DEFAULT_NAME;
    }
}
