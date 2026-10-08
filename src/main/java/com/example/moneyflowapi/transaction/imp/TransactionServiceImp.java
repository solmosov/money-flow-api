package com.example.moneyflowapi.transaction.imp;

import com.example.moneyflowapi.category.CategoryRepository;
import com.example.moneyflowapi.category.CategoryType;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.transaction.*;
import com.example.moneyflowapi.transaction.dto.request.TransactionExpenseRequest;
import com.example.moneyflowapi.transaction.dto.request.TransactionIncomeRequest;
import com.example.moneyflowapi.transaction.dto.response.TransactionDailyResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionResponse;
import com.example.moneyflowapi.transaction.mapper.TransactionMapper;
import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.user.UserRepository;
import com.example.moneyflowapi.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.ZoneId;
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
    private final UserService userService;

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
    public Page<TransactionDailyResponse> findAllByDaily(Instant from, Instant to, String timezone, Pageable pageable) {
        UUID userId = SecurityUtil.getCurrentUserId();

        ZoneId zoneId = ZoneId.of(timezone);

        Pageable pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );


        return transactionRepo.findDailyTransactions(
                        userId,
                        from,
                        to,
                        zoneId,
                        pageRequest
                )
                .map(row -> TransactionDailyResponse.builder()
                        .day(row.getDay())
                        .totalIncome(row.getTotalIncome())
                        .totalExpense(row.getTotalExpense())
                        .total(row.getTotal())
                        .items(parseTransactions(row.getTransactions()))
                        .build()
                );
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UUID userId = SecurityUtil.getCurrentUserId();

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Transaction transaction = transactionRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANS_NOT_FOUND));

        user.balanceDecrement(transaction.getAmount());

        int deleted = transactionRepo.deleteByIdAndUserId(id, userId);

        if(deleted == 0){
            throw new BusinessException(ErrorCode.TRANS_NOT_FOUND);
        }
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
