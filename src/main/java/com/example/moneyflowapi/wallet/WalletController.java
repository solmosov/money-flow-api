package com.example.moneyflowapi.wallet;

import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.wallet.dto.request.CreateWalletRequest;
import com.example.moneyflowapi.wallet.dto.request.UpdateRateAmountRequest;
import com.example.moneyflowapi.wallet.dto.request.UpdateWalletRequest;
import com.example.moneyflowapi.wallet.dto.response.WalletResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Wallets", description = "Wallet management")
@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse createWallet(@Valid @RequestBody CreateWalletRequest request) {
        return walletService.createWallet(request);
    }

    @GetMapping
    public List<WalletResponse> getAll(
            @RequestParam(value = "is_archived", required = false) Boolean isArchive
    ) {
        return walletService.getAll(isArchive);
    }

    @GetMapping("{id}")
    public WalletResponse getOne(
            @PathVariable("id") UUID id
    ) {
        return walletService.getOne(id);
    }

    @PutMapping("/{id}")
    public WalletResponse update(@PathVariable UUID id,
                                        @Valid @RequestBody UpdateWalletRequest request) {
        return walletService.update(id, request);
    }

    @PatchMapping("/{id}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archiveWallet(@PathVariable UUID id) {
        walletService.archiveWallet(id);
    }

    @PutMapping("/{id}/rate-amount")
    public ResponseEntity<WalletResponse> updateRateAmount(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateRateAmountRequest request
    ){
        return ResponseEntity.ok(walletService.updateRateAmount(
                SecurityUtil.getCurrentUserId(),
                id,
                request
        ));
    }
}