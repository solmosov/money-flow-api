package com.example.moneyflowapi.account.imp;

import com.example.moneyflowapi.account.AccountDeletionCodeStore;
import com.example.moneyflowapi.account.AccountDeletionService;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.telegram.TelegramNotifier;
import com.example.moneyflowapi.user.UserRepository;
import com.example.moneyflowapi.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountDeletionServiceImp implements AccountDeletionService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final AccountDeletionCodeStore codeStore;
    private final TelegramNotifier telegramNotifier;
    private final UserService userService;
    private final UserRepository userRepo;

    @Override
    public void requestCode(UUID userId) {
        Long telegramUserId = userRepo.findTelegramUserIdById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TELEGRAM_NOT_LINKED));

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        codeStore.issue(userId, code);

        telegramNotifier.send(
                telegramUserId,
                "Your account deletion code: <tg-spoiler>" + code + "</tg-spoiler>\nValid for 5 minutes."
        );
    }

    @Override
    public void confirm(UUID userId, String code) {
        AccountDeletionCodeStore.Entry entry = codeStore.get(userId);
        if(entry == null){
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        if(entry.attempts().incrementAndGet() > AccountDeletionCodeStore.MAX_ATTEMPTS){
            codeStore.invalidate(userId);
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_ATTEMPTS_EXCEEDED);
        }

        boolean matches = MessageDigest.isEqual(
                entry.code().getBytes(StandardCharsets.UTF_8),
                code.getBytes(StandardCharsets.UTF_8)
        );
        if(!matches){
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_INVALID);
        }

        codeStore.invalidate(userId);
        userService.deleteAccount(userId);
    }
}
