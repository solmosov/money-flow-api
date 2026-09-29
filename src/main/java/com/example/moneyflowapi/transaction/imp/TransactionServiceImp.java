package com.example.moneyflowapi.transaction.imp;

import com.example.moneyflowapi.category.CategoryRepository;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.transaction.Transaction;
import com.example.moneyflowapi.transaction.TransactionRepository;
import com.example.moneyflowapi.transaction.TransactionService;
import com.example.moneyflowapi.transaction.TransactionType;
import com.example.moneyflowapi.transaction.dto.request.TransactionExpenseRequest;
import com.example.moneyflowapi.transaction.dto.request.TransactionIncomeRequest;
import com.example.moneyflowapi.transaction.dto.response.TransactionResponse;
import com.example.moneyflowapi.transaction.mapper.TransactionMapper;
import com.example.moneyflowapi.user.UserRepository;
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

    @Override
    public TransactionResponse createIncome(TransactionIncomeRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();

        var saved = transactionRepo.save(
                Transaction.builder()
                        .user(userRepo.getReferenceById(userId))
                        .type(TransactionType.INCOME)
                        .amount(request.amount())
                        .currency(request.currency())
                        .category(categoryRepository.getReferenceById(request.categoryId()))
                        .date(request.date())
                        .description(request.description())
                        .build()
        );

        return mapper.toDto(getById(saved.getId()));
    }

    @Override
    public TransactionResponse createExpense(TransactionExpenseRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();

        var saved = transactionRepo.save(
                Transaction.builder()
                        .user(userRepo.getReferenceById(userId))
                        .type(TransactionType.EXPENSE)
                        .amount(request.amount())
                        .currency(request.currency())
                        .category(categoryRepository.getReferenceById(request.categoryId()))
                        .date(request.date())
                        .description(request.description())
                        .build()
        );

        return mapper.toDto(getById(saved.getId()));
    }


    private Transaction getById(UUID id) {
        return transactionRepo.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANS_NOT_FOUND));
    }

    @Override
    public List<TransactionResponse> findAll() {
        return mapper.toList(transactionRepo.findAll());
    }
}
