package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.transaction.dto.request.TransactionExpenseRequest;
import com.example.moneyflowapi.transaction.dto.request.TransactionIncomeRequest;
import com.example.moneyflowapi.transaction.dto.response.TransactionDailyResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/income")
    public ResponseEntity<TransactionResponse> createIncome(
            @Valid
            @RequestBody TransactionIncomeRequest request
    ) {
        return ResponseEntity.ok(transactionService.createIncome(request));
    }

    @PostMapping("expense")
    public ResponseEntity<TransactionResponse> createExpense(
            @Valid
            @RequestBody TransactionExpenseRequest request
    ) {
        return ResponseEntity.ok(transactionService.createExpense(request));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> findAll(){
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping("daily")
    public ResponseEntity<List<TransactionDailyResponse>> findAllByDaily(){
        return ResponseEntity.ok(transactionService.findAllByDaily());
    }

}
