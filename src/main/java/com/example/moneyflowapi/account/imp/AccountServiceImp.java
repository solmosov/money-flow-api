package com.example.moneyflowapi.account.imp;

import com.example.moneyflowapi.account.Account;
import com.example.moneyflowapi.account.AccountRepository;
import com.example.moneyflowapi.account.AccountService;
import com.example.moneyflowapi.account.dto.request.CreateAccountRequest;
import com.example.moneyflowapi.account.dto.request.UpdateAccountRequest;
import com.example.moneyflowapi.account.dto.response.AccountResponse;
import com.example.moneyflowapi.account.mapper.AccountMapper;
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
public class AccountServiceImp implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper mapper;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;

    @Override
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();

        var newAccount = Account.builder()
                .user(userRepository.getReferenceById(userId))
                .name(request.name())
                .type(request.type())
                .currency(currencyRepository.getReferenceById(request.currencyCode()))
                .balance(request.initialBalance())
                .build();

        return mapper.toDto(accountRepository.save(newAccount));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> myAccounts() {
        UUID userId = SecurityUtil.getCurrentUserId();

        return accountRepository.findAllByUserIdAndArchivedAtIsNull(userId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(UUID id, UpdateAccountRequest request) {
        Account account = findOwnedAccount(id);
        account.setName(request.name());

        return mapper.toDto(account);
    }

    @Override
    @Transactional
    public void archiveAccount(UUID id) {
        Account account = findOwnedAccount(id);
        account.setArchivedAt(Instant.now());
    }

    private Account findOwnedAccount(UUID id) {
        UUID userId = SecurityUtil.getCurrentUserId();

        return accountRepository.findByIdAndUserIdAndArchivedAtIsNull(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
    }
}