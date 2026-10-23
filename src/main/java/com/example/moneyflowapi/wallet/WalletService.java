package com.example.moneyflowapi.wallet;

import com.example.moneyflowapi.wallet.dto.request.CreateWalletRequest;
import com.example.moneyflowapi.wallet.dto.request.UpdateWalletRequest;
import com.example.moneyflowapi.wallet.dto.response.WalletResponse;

import java.util.List;
import java.util.UUID;

public interface WalletService {

    WalletResponse createAccount(CreateWalletRequest request);

    List<WalletResponse> myAccounts();

    WalletResponse updateAccount(UUID id, UpdateWalletRequest request);

    void archiveAccount(UUID id);
}