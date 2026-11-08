package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.transaction.dto.request.*;
import com.example.moneyflowapi.transaction.dto.response.DailyTotalResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionYearsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionResponse createIncome(TransactionIncomeRequest request);
    TransactionResponse createIncome(UUID userId, TransactionIncomeRequest request);
    TransactionResponse updateIncome(UUID id, TransactionUpdateIncomeRequest request);
    TransactionResponse updateIncome(UUID userId, UUID id, TransactionUpdateIncomeRequest request);

    TransactionResponse createExpense(TransactionExpenseRequest request);
    TransactionResponse createExpense(UUID userId, TransactionExpenseRequest request);
    TransactionResponse updateExpense(UUID id, TransactionUpdateExpenseRequest request);
    TransactionResponse updateExpense(UUID userId, UUID id, TransactionUpdateExpenseRequest request);

    TransactionResponse createTransfer(TransactionTransferRequest request);

    Page<TransactionResponse> findAll(Pageable pageable);
    Slice<TransactionResponse> findFeed(Instant from, Instant to, Pageable pageable);
    List<DailyTotalResponse> findDailyTotals(Instant from, Instant to, String timezone);

    void delete(UUID id);
    void delete(UUID userId, UUID id);

    TransactionYearsResponse getAvailableYears(UUID userId);
}