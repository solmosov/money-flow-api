package com.example.moneyflowapi.transaction.imp;

import com.example.moneyflowapi.category.CategoryRepository;
import com.example.moneyflowapi.category.CategoryType;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.transaction.Transaction;
import com.example.moneyflowapi.transaction.TransactionRepository;
import com.example.moneyflowapi.transaction.TransactionService;
import com.example.moneyflowapi.transaction.TransactionType;
import com.example.moneyflowapi.transaction.dto.request.*;
import com.example.moneyflowapi.transaction.dto.response.*;
import com.example.moneyflowapi.transaction.mapper.TransactionMapper;
import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.user.UserRepository;
import com.example.moneyflowapi.wallet.Wallet;
import com.example.moneyflowapi.wallet.WalletRepository;
import com.example.moneyflowapi.wallet.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImp implements TransactionService {

    private final TransactionRepository transactionRepo;
    private final TransactionMapper mapper;
    private final UserRepository userRepo;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;
    private final WalletService walletService;

    private static final int MAX_DAILY_TOTALS_DAYS = 20;

    @Transactional
    @Override
    public TransactionResponse createIncome(TransactionIncomeRequest request) {
        validateCategory(request.categoryId(), CategoryType.INCOME);

        UUID userId = SecurityUtil.getCurrentUserId();

        userRepo.updateTransactionTimestamps(userId);

        Wallet wallet = walletService.findWalletForUpdate(request.walletId(), userId);


        wallet.balanceIncrement(request.amount());
        walletRepository.save(wallet);

        var saved = transactionRepo.save(
                Transaction.builder()
                        .user(userRepo.getReferenceById(userId))
                        .wallet(wallet)
                        .category(categoryRepository.getReferenceById(request.categoryId()))
                        .type(TransactionType.INCOME)
                        .amount(request.amount())
                        .rateAmount(wallet.getRateAmount())
                        .currency(wallet.getCurrency())
                        .date(request.date())
                        .description(request.description())
                        .build()
        );

        return mapper.toDto(getById(saved.getId()));
    }

    @Transactional
    @Override
    public TransactionResponse updateIncome(UUID id, TransactionUpdateIncomeRequest request) {
        Transaction transaction = getByIdAndOwner(id);

        Wallet wallet = transaction.getWallet();

        if (wallet.getArchivedAt() != null) {
            throw new BusinessException(ErrorCode.WALLET_IS_ARCHIVED);
        }

        if (request.categoryId() != null) {
            validateCategory(request.categoryId(), CategoryType.INCOME);
            transaction.setCategory(categoryRepository.getReferenceById(request.categoryId()));
        }

        if (request.amount() != null) {
            wallet.balanceDecrement(transaction.getAmount());
            wallet.balanceIncrement(request.amount());

            transaction.setAmount(request.amount());
        }

        if (request.date() != null) {
            transaction.setDate(request.date());
        }

        if (request.description() != null) {
            transaction.setDescription(request.description());
        }

        walletRepository.save(wallet);
        transactionRepo.save(transaction);

        return mapper.toDto(transaction);
    }

    @Transactional
    @Override
    public TransactionResponse createExpense(TransactionExpenseRequest request) {
        validateCategory(request.categoryId(), CategoryType.EXPENSE);

        UUID userId = SecurityUtil.getCurrentUserId();

        userRepo.updateTransactionTimestamps(userId);

        Wallet wallet = walletService.findWalletForUpdate(request.walletId(), userId);

        wallet.balanceDecrement(request.amount());
        walletRepository.save(wallet);

        var saved = transactionRepo.save(
                Transaction.builder()
                        .user(userRepo.getReferenceById(userId))
                        .wallet(wallet)
                        .category(categoryRepository.getReferenceById(request.categoryId()))
                        .type(TransactionType.EXPENSE)
                        .amount(request.amount())
                        .rateAmount(wallet.getRateAmount())
                        .currency(wallet.getCurrency())
                        .date(request.date())
                        .description(request.description())
                        .build()
        );

        return mapper.toDto(getById(saved.getId()));
    }

    @Transactional
    @Override
    public TransactionResponse updateExpense(UUID id, TransactionUpdateExpenseRequest request) {
        Transaction transaction = getByIdAndOwner(id);

        Wallet wallet = transaction.getWallet();

        if (wallet.getArchivedAt() != null) {
            throw new BusinessException(ErrorCode.WALLET_IS_ARCHIVED);
        }

        if (request.categoryId() != null) {
            validateCategory(request.categoryId(), CategoryType.EXPENSE);
            transaction.setCategory(categoryRepository.getReferenceById(request.categoryId()));
        }

        if (request.amount() != null) {
            wallet.balanceIncrement(transaction.getAmount());
            wallet.balanceDecrement(request.amount());

            transaction.setAmount(request.amount());
        }

        if (request.date() != null) {
            transaction.setDate(request.date());
        }

        if (request.description() != null) {
            transaction.setDescription(request.description());
        }

        walletRepository.save(wallet);
        transactionRepo.save(transaction);

        return mapper.toDto(transaction);
    }

    @Transactional
    @Override
    public TransactionResponse createTransfer(TransactionTransferRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();

        userRepo.updateTransactionTimestamps(userId);

        Wallet fromWallet = walletService.findWalletForUpdate(request.fromWalletId(), userId);
        Wallet toWallet = walletService.findWalletForUpdate(request.toWalletId(), userId);

        BigDecimal rate = request.toAmount().divide(request.fromAmount(), 6, RoundingMode.HALF_UP);

        if (fromWallet.getBalance().compareTo(request.fromAmount()) < 0) {
            throw new BusinessException(ErrorCode.WALLET_BALANCE_INSUFFICIENT);
        }

        fromWallet.balanceDecrement(request.fromAmount());
        toWallet.balanceIncrement(request.toAmount());

        Transaction newTransaction = Transaction.builder()
                .user(userRepo.getReferenceById(userId))
                .wallet(fromWallet)
                .toWallet(toWallet)
                .type(TransactionType.TRANSFER)
                .amount(request.fromAmount())
                .toAmount(request.toAmount())
                .rateAmount(rate)
                .currency(fromWallet.getCurrency())
                .date(request.date())
                .description(request.description())
                .build();


        var saved = transactionRepo.save(newTransaction);

        return mapper.toDto(getByIdAndOwner(saved.getId()));
    }

    private Transaction getByIdAndOwner(UUID id) {
        return transactionRepo.findByIdAndUserId(id, SecurityUtil.getCurrentUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANS_NOT_FOUND));
    }

    @Override
    public Page<TransactionResponse> findAll(Pageable pageable) {
        UUID userId = SecurityUtil.getCurrentUserId();

        return mapper.toPage(transactionRepo.findAllByUserId(userId, pageable));
    }

    @Override
    public Slice<TransactionResponse> findFeed(Instant from, Instant to, Pageable pageable) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Pageable pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return transactionRepo.findFeedTransactions(userId, from, to, pageRequest)
                .map(mapper::toDto);
    }

    @Override
    public List<DailyTotalResponse> findDailyTotals(Instant from, Instant to, String timezone) {
        UUID userId = SecurityUtil.getCurrentUserId();
        ZoneId zoneId = ZoneId.of(timezone);

        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("'from' must be before 'to'");
        }

        long days = ChronoUnit.DAYS.between(
                from.atZone(zoneId).toLocalDate(),
                to.atZone(zoneId).toLocalDate()
        );

        if (days > MAX_DAILY_TOTALS_DAYS) {
            throw new BusinessException(ErrorCode.TRANSACTION_DAILY_TOTAL_RANGE_LIMIT, MAX_DAILY_TOTALS_DAYS);
        }

        return transactionRepo.findDailyTotals(userId, from, to, zoneId.getId())
                .stream()
                .map(row -> new DailyTotalResponse(
                        row.getDay(),
                        row.getTotalIncome(),
                        row.getTotalExpense(),
                        row.getTotalIncome().subtract(row.getTotalExpense())
                ))
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Transaction transaction = transactionRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANS_NOT_FOUND));

        if (transaction.getType() == TransactionType.INCOME) {
            transaction.getWallet().balanceDecrement(transaction.getAmount());
        } else if (transaction.getType() == TransactionType.EXPENSE) {
            transaction.getWallet().balanceIncrement(transaction.getAmount());
        } else if(transaction.getType() == TransactionType.TRANSFER){
            transaction.getWallet().balanceIncrement(transaction.getAmount());
            transaction.getToWallet().balanceDecrement(transaction.getToAmount());
        }else {
            throw new IllegalStateException("Not supported the transaction type: %s".formatted(transaction.getType()));
        }

        int deleted = transactionRepo.deleteByIdAndUserId(id, userId);

        if (deleted == 0) {
            throw new BusinessException(ErrorCode.TRANS_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public TransactionYearsResponse getAvailableYears(UUID userId) {
        return new TransactionYearsResponse(transactionRepo.findDistinctYearsByUserId(userId));
    }

    private List<TransactionDailyResponse.Item> parseTransactions(String json) {
        try {
            return objectMapper.readValue(
                    json,
                    new TypeReference<>() {
                    }
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse transactions JSON", e);
        }
    }

    private Transaction getById(UUID id) {
        return transactionRepo.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANS_NOT_FOUND));
    }

    private void validateCategory(UUID categoryId, CategoryType type) {
        int updated = categoryRepository.updateUsedAt(
                categoryId,
                SecurityUtil.getCurrentUserId(),
                type,
                Instant.now()
        );

        if (updated == 0) {
            throw new BusinessException(ErrorCode.TRANSACTION_CATEGORY_INVALID);
        }
    }
}
