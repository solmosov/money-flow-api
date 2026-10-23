package com.example.moneyflowapi.wallet.imp;

import com.example.moneyflowapi.wallet.Wallet;
import com.example.moneyflowapi.wallet.WalletRepository;
import com.example.moneyflowapi.wallet.WalletService;
import com.example.moneyflowapi.wallet.dto.request.CreateWalletRequest;
import com.example.moneyflowapi.wallet.dto.request.UpdateWalletRequest;
import com.example.moneyflowapi.wallet.dto.response.WalletResponse;
import com.example.moneyflowapi.wallet.mapper.WalletMapper;
import com.example.moneyflowapi.currency.CurrencyRepository;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImp implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper mapper;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;

    @Override
    @Transactional
    public WalletResponse createAccount(CreateWalletRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();

        var newAccount = Wallet.builder()
                .user(userRepository.getReferenceById(userId))
                .name(request.name())
                .type(request.type())
                .currency(currencyRepository.getReferenceById(request.currencyCode()))
                .rateAmount(request.rateAmount())
                .balance(request.initialBalance())
                .build();

        return mapper.toDto(walletRepository.save(newAccount));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletResponse> myAccounts() {
        UUID userId = SecurityUtil.getCurrentUserId();

        return walletRepository.findAllByUserIdAndArchivedAtIsNull(userId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public WalletResponse updateAccount(UUID id, UpdateWalletRequest request) {
        Wallet wallet = findOwnedAccount(id);
        wallet.setName(request.name());

        return mapper.toDto(wallet);
    }

    @Override
    @Transactional
    public void archiveAccount(UUID id) {
        Wallet wallet = findOwnedAccount(id);
        wallet.setArchivedAt(Instant.now());
    }

    private Wallet findOwnedAccount(UUID id) {
        UUID userId = SecurityUtil.getCurrentUserId();

        return walletRepository.findByIdAndUserIdAndArchivedAtIsNull(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
    }
}