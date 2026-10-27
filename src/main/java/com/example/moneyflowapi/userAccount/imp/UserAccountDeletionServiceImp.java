package com.example.moneyflowapi.userAccount.imp;

import com.example.moneyflowapi.userAccount.UserAccountDeletionCodeStore;
import com.example.moneyflowapi.userAccount.UserAccountDeletionService;
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
public class UserAccountDeletionServiceImp implements UserAccountDeletionService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserAccountDeletionCodeStore codeStore;
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
                "Your account deletion code: <tg-spoiler>" + code + "</tg-spoiler>\nValid for 2 minutes."
        );
    }

    @Override
    public void confirm(UUID userId, String code) {
        UserAccountDeletionCodeStore.Entry entry = codeStore.get(userId);
        if(entry == null){
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        if(entry.attempts().incrementAndGet() > UserAccountDeletionCodeStore.MAX_ATTEMPTS){
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
