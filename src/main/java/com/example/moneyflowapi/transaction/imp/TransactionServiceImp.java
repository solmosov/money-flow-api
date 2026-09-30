package com.example.moneyflowapi.transaction.imp;

import com.example.moneyflowapi.category.CategoryRepository;
import com.example.moneyflowapi.category.CategoryType;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.transaction.*;
import com.example.moneyflowapi.transaction.dto.request.TransactionExpenseRequest;
import com.example.moneyflowapi.transaction.dto.request.TransactionIncomeRequest;
import com.example.moneyflowapi.transaction.dto.response.TransactionResponse;
import com.example.moneyflowapi.transaction.mapper.TransactionMapper;
import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImp implements TransactionService {

    private final TransactionRepository transactionRepo;
    private final TransactionMapper mapper;
    private final UserRepository userRepo;
    private final CategoryRepository categoryRepository;

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
    public List<TransactionResponse> findAll() {
        return mapper.toList(transactionRepo.findAll());
    }

    private Transaction getById(UUID id) {
        return transactionRepo.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANS_NOT_FOUND));
    }

    private void validateCategory(UUID categoryId, CategoryType type) {
        if(!categoryRepository.existsByIdAndType(categoryId, type)){
            throw new BusinessException(ErrorCode.TRANSACTION_CATEGORY_INVALID);
        }
    }
}
