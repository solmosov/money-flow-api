package com.example.moneyflowapi.account;

import com.example.moneyflowapi.account.dto.request.CreateAccountRequest;
import com.example.moneyflowapi.account.dto.request.UpdateAccountRequest;
import com.example.moneyflowapi.account.dto.response.AccountResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Accounts", description = "Account management")
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping
    public List<AccountResponse> myAccounts() {
        return accountService.myAccounts();
    }

    @PutMapping("/{id}")
    public AccountResponse updateAccount(@PathVariable UUID id,
                                         @Valid @RequestBody UpdateAccountRequest request) {
        return accountService.updateAccount(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archiveAccount(@PathVariable UUID id) {
        accountService.archiveAccount(id);
    }
}