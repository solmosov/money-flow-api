package com.example.moneyflowapi.wallet;

import com.example.moneyflowapi.wallet.dto.request.CreateWalletRequest;
import com.example.moneyflowapi.wallet.dto.request.UpdateWalletRequest;
import com.example.moneyflowapi.wallet.dto.response.WalletResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Accounts", description = "Account management")
@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse createAccount(@Valid @RequestBody CreateWalletRequest request) {
        return walletService.createAccount(request);
    }

    @GetMapping
    public List<WalletResponse> myAccounts() {
        return walletService.myAccounts();
    }

    @PutMapping("/{id}")
    public WalletResponse updateAccount(@PathVariable UUID id,
                                        @Valid @RequestBody UpdateWalletRequest request) {
        return walletService.updateAccount(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archiveAccount(@PathVariable UUID id) {
        walletService.archiveAccount(id);
    }
}