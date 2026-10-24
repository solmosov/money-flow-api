package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.transaction.dto.request.TransactionExpenseRequest;
import com.example.moneyflowapi.transaction.dto.request.TransactionIncomeRequest;
import com.example.moneyflowapi.transaction.dto.request.TransactionUpdateExpenseRequest;
import com.example.moneyflowapi.transaction.dto.request.TransactionUpdateIncomeRequest;
import com.example.moneyflowapi.transaction.dto.response.DailyTotalResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionFeedResponse;
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
    TransactionResponse updateIncome(UUID id, TransactionUpdateIncomeRequest request);

    TransactionResponse createExpense(TransactionExpenseRequest request);
    TransactionResponse updateExpense(UUID id, TransactionUpdateExpenseRequest request);

    Page<TransactionResponse> findAll(Pageable pageable);
    Slice<TransactionFeedResponse> findFeed(Instant from, Instant to, Pageable pageable);
    List<DailyTotalResponse> findDailyTotals(Instant from, Instant to, String timezone);

    void delete(UUID id);

    TransactionYearsResponse getAvailableYears(UUID userId);

}