package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.transaction.dto.request.TransactionExpenseRequest;
import com.example.moneyflowapi.transaction.dto.request.TransactionIncomeRequest;
import com.example.moneyflowapi.transaction.dto.response.TransactionDailyResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionResponse createIncome(TransactionIncomeRequest request);
    TransactionResponse createExpense(TransactionExpenseRequest request);

    Page<TransactionResponse> findAll(Pageable pageable);
    Page<TransactionDailyResponse> findAllByDaily(Instant from, Instant to, String timezone, Pageable pageable);

    void delete(UUID id);
 }
