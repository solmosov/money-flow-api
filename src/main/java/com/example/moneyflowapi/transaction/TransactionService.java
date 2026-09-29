package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.transaction.dto.request.TransactionIncomeRequest;
import com.example.moneyflowapi.transaction.dto.response.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse createIncome(TransactionIncomeRequest request);
    TransactionResponse createExpense(TransactionIncomeRequest request);

    List<TransactionResponse> findAll();
 }
