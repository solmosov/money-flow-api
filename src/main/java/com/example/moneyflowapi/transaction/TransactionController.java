package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.category.CategoryType;
import com.example.moneyflowapi.common.pageable.PageResponseMapper;
import com.example.moneyflowapi.common.response.PageResponse;
import com.example.moneyflowapi.common.response.SliceResponse;
import com.example.moneyflowapi.common.response.SliceResponseMapper;
import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.transaction.dto.request.TransactionExpenseRequest;
import com.example.moneyflowapi.transaction.dto.request.TransactionIncomeRequest;
import com.example.moneyflowapi.transaction.dto.response.*;
import com.example.moneyflowapi.transaction.enums.StatsType;
import com.example.moneyflowapi.transaction.projection.TransactionDiffProjection;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Tag(name = "Transaction", description = "Transaction management")
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionStatService transactionStatService;
    private final TransactionRepository transactionRepository;

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

    @GetMapping("/years")
    public TransactionYearsResponse getAvailableYears() {
        return transactionService.getAvailableYears(SecurityUtil.getCurrentUserId());
    }

    @GetMapping("feed")
    public ResponseEntity<SliceResponse<TransactionListItemResponse>> findFeed(
            @RequestParam(value = "from", required = false) Instant from,
            @RequestParam(value = "to", required = false) Instant to,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Slice<TransactionListItemResponse> slice = transactionService.findFeed(from, to, pageable);

        return ResponseEntity.ok(SliceResponseMapper.map(slice));
    }

    @GetMapping("daily-totals")
    public ResponseEntity<List<DailyTotalResponse>> findDailyTotals(
            @RequestParam("from") Instant from,
            @RequestParam("to") Instant to,
            @RequestHeader("X-Timezone") String timezone
    ) {
        return ResponseEntity.ok(transactionService.findDailyTotals(from, to, timezone));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") UUID id
    ) {
        transactionService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/stats/income-expense")
    public ResponseEntity<TransactionIncomeExpenseStatResponse> getIncomeExpenseStats(
            @RequestParam(value = "type", required = false, defaultValue = "WEEK") StatsType type,
            @RequestParam(value = "from") Instant from,
            @RequestHeader("X-Timezone") String timezone
    ) {
        return ResponseEntity.ok(transactionStatService.getIncomeExpenseStats(type, from, ZoneId.of(timezone)));
    }

    @GetMapping("/stats/category-totals")
    public ResponseEntity<TransactionCategoryExpenseStatResponse> getCategoryExpenseStats(
            @RequestParam(value = "type", required = false, defaultValue = "WEEK") StatsType type,
            @RequestParam(value = "category_type", required = false, defaultValue = "EXPENSE") CategoryType categoryType,
            @RequestParam(value = "from") Instant from,
            @RequestHeader("X-Timezone") String timezone
    ) {
        return ResponseEntity.ok(transactionStatService.getCategoryTotalsStatsByType(type, categoryType, from, ZoneId.of(timezone)));
    }


//    @GetMapping("/stats/category-expense-by-interval")
//    public ResponseEntity<TransactionCategoryExpenseStatsByIntervalResponse> getCategoryExpenseStatsByInterval(
//            @RequestParam(value = "type", required = false, defaultValue = "WEEK") StatsType type,
//            @RequestParam(value = "from") Instant from,
//            @RequestHeader("X-Timezone") String timezone
//    ){
//        ZoneId.of(timezone);
//        return ResponseEntity.ok(transactionStatService.getCategoryExpenseStatsByInterval(type, from, timezone));
//    }

    @GetMapping("/stats/month-totals")
    public ResponseEntity<TransactionMonthTotalsStatResponse> getMonthTotalsStat(
            @RequestParam(value = "from") Instant from,
            @RequestHeader("X-Timezone") String timezone
    ) {
        return ResponseEntity.ok(transactionStatService.getMonthTotalsStats(from, ZoneId.of(timezone)));
    }

    @GetMapping("/stats/diff")
    public TransactionDiffStatResponse getDiffStats(
            @RequestParam(value = "from") Instant from,
            @RequestHeader("X-Timezone") String timezone
    ){
        return transactionStatService.getDiffStats(from, ZoneId.of(timezone));

    }
}
