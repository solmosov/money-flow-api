package com.example.moneyflowapi.transaction.imp;

import com.example.moneyflowapi.category.CategoryRepository;
import com.example.moneyflowapi.category.CategoryType;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.transaction.*;
import com.example.moneyflowapi.transaction.dto.request.TransactionExpenseRequest;
import com.example.moneyflowapi.transaction.dto.request.TransactionIncomeRequest;
import com.example.moneyflowapi.transaction.dto.response.*;
import com.example.moneyflowapi.transaction.mapper.TransactionMapper;
import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

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
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    private static final int MAX_DAILY_TOTALS_DAYS = 20;

    @Transactional
    @Override
    public TransactionResponse createIncome(TransactionIncomeRequest request) {
        validateCategory(request.categoryId(), CategoryType.INCOME);

        UUID userId = SecurityUtil.getCurrentUserId();

        User user = userRepo.findByIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));


        user.balanceIncrement(request.amount());

        userRepo.save(user);

        var saved = transactionRepo.save(
                Transaction.builder()
                        .user(user)
                        .type(TransactionType.INCOME)
                        .amount(request.amount())
                        .currency(TransactionCurrency.UZS)
                        .category(categoryRepository.getReferenceById(request.categoryId()))
                        .date(request.date())
                        .description(request.description())
                        .build()
        );

        return mapper.toDto(getById(saved.getId()));
    }

    @Transactional
    @Override
    public TransactionResponse createExpense(TransactionExpenseRequest request) {
        validateCategory(request.categoryId(), CategoryType.EXPENSE);

        UUID userId = SecurityUtil.getCurrentUserId();

        User user = userRepo.findByIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.balanceDecrement(request.amount());

        userRepo.save(user);

        var saved = transactionRepo.save(
                Transaction.builder()
                        .user(user)
                        .type(TransactionType.EXPENSE)
                        .amount(request.amount())
                        .currency(TransactionCurrency.UZS)
                        .category(categoryRepository.getReferenceById(request.categoryId()))
                        .date(request.date())
                        .description(request.description())
                        .build()
        );

        return mapper.toDto(getById(saved.getId()));
    }

    @Override
    public Page<TransactionResponse> findAll(Pageable pageable) {
        UUID userId = SecurityUtil.getCurrentUserId();

        return mapper.toPage(transactionRepo.findAllByUserId(userId, pageable));
    }

    @Override
    public Slice<TransactionListItemResponse> findFeed(Instant from, Instant to, Pageable pageable) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Pageable pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return transactionRepo.findFeedTransactions(userId, from, to, pageRequest)
                .map(row -> new TransactionListItemResponse(
                        row.getId(),
                        row.getCategoryName(),
                        row.getCategoryIconName(),
                        row.getAmount(),
                        row.getCurrency(),
                        row.getDescription(),
                        TransactionType.valueOf(row.getType()),
                        row.getDate(),
                        row.getCreatedAt(),
                        row.getUpdatedAt()
                ));
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

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Transaction transaction = transactionRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANS_NOT_FOUND));

        if(transaction.getType() == TransactionType.INCOME){
            user.balanceDecrement(transaction.getAmount());
        }else if(transaction.getType() == TransactionType.EXPENSE){
            user.balanceIncrement(transaction.getAmount());
        }else {
            throw new IllegalStateException("Not supported the transaction type: %s".formatted(transaction.getType()));
        }

        userRepo.save(user);

        int deleted = transactionRepo.deleteByIdAndUserId(id, userId);

        if(deleted == 0){
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
        if (!categoryRepository.existsByIdAndTypeAndDeletedAtIsNull(categoryId, type)) {
            throw new BusinessException(ErrorCode.TRANSACTION_CATEGORY_INVALID);
        }
    }
}
