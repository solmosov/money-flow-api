package com.example.moneyflowapi.demo;

import com.example.moneyflowapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoUserResolver {

    private static final String DEFAULT_LANGUAGE = "en";

    private static final Map<String, String> DEMO_EMAILS = Map.of(
            "en", "user_en@demo.dev",
            "uz", "user_uz@demo.dev",
            "ru", "user_ru@demo.dev"
    );

    private final UserRepository userRepository;
    private final Map<String, UUID> cache = new ConcurrentHashMap<>();

    public UUID resolve(String acceptLanguage) {
        String language = resolveLanguage(acceptLanguage);
        return cache.computeIfAbsent(language, this::findDemoUserId);
    }

    private UUID findDemoUserId(String language) {
        String email = DEMO_EMAILS.get(language);
        return userRepository.findIdByEmail(email).orElseThrow(() -> {
            log.error("Demo user not found: {}", email);
            return new IllegalStateException("Demo user not found: " + email);
        });
    }

    private String resolveLanguage(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.isBlank()) {
            return DEFAULT_LANGUAGE;
        }
        try {
            for (Locale.LanguageRange range : Locale.LanguageRange.parse(acceptLanguage)) {
                String tag = range.getRange().toLowerCase(Locale.ROOT);
                int dash = tag.indexOf('-');
                String language = dash > 0 ? tag.substring(0, dash) : tag;
                if (DEMO_EMAILS.containsKey(language)) {
                    return language;
                }
            }
        } catch (IllegalArgumentException ignored) {
        }
        return DEFAULT_LANGUAGE;
    }
}