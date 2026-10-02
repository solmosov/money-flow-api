package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.common.pageable.PageResponseMapper;
import com.example.moneyflowapi.common.response.PageResponse;
import com.example.moneyflowapi.transaction.dto.request.TransactionExpenseRequest;
import com.example.moneyflowapi.transaction.dto.request.TransactionIncomeRequest;
import com.example.moneyflowapi.transaction.dto.response.TransactionCategoryExpenseStatResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionDailyResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionIncomeExpenseStatResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionResponse;
import com.example.moneyflowapi.transaction.enums.StatsType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionStatService transactionStatService;

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
    public ResponseEntity<PageResponse<TransactionResponse>> findAll(Pageable pageable) {
        Page<TransactionResponse> page = transactionService.findAll(pageable);
        return ResponseEntity.ok(PageResponseMapper.map(page));
    }

    @GetMapping("daily")
    public ResponseEntity<PageResponse<TransactionDailyResponse>> findAllByDaily(
            @RequestParam(value = "from", required = false) Instant from,
            @RequestParam(value = "to", required = false) Instant to,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<TransactionDailyResponse> page = transactionService.findAllByDaily(
                from,
                to,
                pageable
        );

        return ResponseEntity.ok(PageResponseMapper.map(page));
    }

    @GetMapping("/stats/income-expense")
    public ResponseEntity<TransactionIncomeExpenseStatResponse> getIncomeExpenseStats(
            @RequestParam(value = "type", required = false, defaultValue = "WEEK") StatsType type,
            @RequestParam(value = "from") Instant from
    ){
        return ResponseEntity.ok(transactionStatService.getIncomeExpenseStats(type, from));
    }

    @GetMapping("/stats/category-expense")
    public ResponseEntity<TransactionCategoryExpenseStatResponse> getCategoryExpenseStats(
            @RequestParam(value = "type", required = false, defaultValue = "WEEK") StatsType type,
            @RequestParam(value = "from") Instant from
    ){
        return ResponseEntity.ok(transactionStatService.getCategoryExpenseStats(type, from));
    }

}
