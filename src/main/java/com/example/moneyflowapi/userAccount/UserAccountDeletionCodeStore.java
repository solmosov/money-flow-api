package com.example.moneyflowapi.userAccount;

import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UserAccountDeletionCodeStore {
    public static final int MAX_ATTEMPTS = 5;
    public static final Duration TTL = Duration.ofMinutes(5);
    public static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);

    private final ConcurrentHashMap<UUID, Entry> store = new ConcurrentHashMap<>();

    public void issue(UUID userId, String code) {
        Instant now = Instant.now();

        store.compute(userId, (id, existing) -> {
            if (existing != null && existing.createdAt().plus(RESEND_COOLDOWN).isAfter(now)) {
                throw new BusinessException(ErrorCode.VERIFICATION_CODE_TOO_FREQUENT);
            }

            return new Entry(code, new AtomicInteger(), now, now.plus(TTL));
        });
    }

    public Entry get(UUID userId) {
        Entry entry = store.get(userId);
        if (entry == null) {
            return null;
        }

        if (entry.expiresAt().isBefore(Instant.now())) {
            store.remove(userId, entry);
            return null;
        }

        return entry;
    }

    public void invalidate(UUID userId) {
        store.remove(userId);
    }

    @Scheduled(fixedDelay = 60_000)
    public void evictExpired() {
        Instant now = Instant.now();
        store.values().removeIf(entry -> entry.expiresAt().isBefore(now));
    }

    public record Entry(String code, AtomicInteger attempts, Instant createdAt, Instant expiresAt) {
    }
}
