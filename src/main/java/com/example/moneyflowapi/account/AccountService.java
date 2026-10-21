package com.example.moneyflowapi.account;

import com.example.moneyflowapi.account.dto.request.CreateAccountRequest;
import com.example.moneyflowapi.account.dto.request.UpdateAccountRequest;
import com.example.moneyflowapi.account.dto.response.AccountResponse;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);

    List<AccountResponse> myAccounts();

    AccountResponse updateAccount(UUID id, UpdateAccountRequest request);

    void archiveAccount(UUID id);
}