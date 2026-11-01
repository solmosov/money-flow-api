package com.example.moneyflowapi.userAccount.imp;

import com.example.moneyflowapi.currency.Currency;
import com.example.moneyflowapi.currency.CurrencyRepository;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.user.UserRepository;
import com.example.moneyflowapi.userAccount.UserAccountService;
import com.example.moneyflowapi.userAccount.dto.request.UpdateBaseCurrencyRequest;
import com.example.moneyflowapi.userAccount.dto.request.UserStartRequest;
import com.example.moneyflowapi.wallet.Wallet;
import com.example.moneyflowapi.wallet.WalletRepository;
import com.example.moneyflowapi.wallet.WalletType;
import com.example.moneyflowapi.wallet.resolver.MainWalletNameResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImp implements UserAccountService {

    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;
    private final MainWalletNameResolver mainWalletNameResolver;
    private final WalletRepository walletRepository;


    @Override
    @Transactional
    public void initialize(UUID userId, String language, UserStartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getInitializedAt() != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_INITIALIZED);
        }

        Currency currency = currencyRepository.findById(request.currencyCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.CURRENCY_NOT_FOUND));

        user.setBaseCurrency(currency);
        user.setInitializedAt(Instant.now());
        user.setOnboardingCompletedAt(Instant.now());

        Wallet wallet = Wallet.builder()
                .user(user)
                .name(mainWalletNameResolver.resolve(language))
                .type(WalletType.CASH)
                .balance(BigDecimal.ZERO)
                .currency(currency)
                .rateAmount(BigDecimal.ONE)
                .build();

        userRepository.save(user);
        walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void changeBaseCurrency(UUID userId, UpdateBaseCurrencyRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Currency currency = currencyRepository.findById(request.currencyCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.CURRENCY_NOT_FOUND));

        user.setBaseCurrency(currency);

        userRepository.save(user);

        walletRepository.markAllRateChangeUnconfirmedByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteDemoData(UUID currentRealUserId) {
        int updated = userRepository.disableDemoUsage(currentRealUserId);

        if (updated == 0) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
    }
}
