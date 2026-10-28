package com.example.moneyflowapi.wallet;

import com.example.moneyflowapi.wallet.dto.request.CreateWalletRequest;
import com.example.moneyflowapi.wallet.dto.request.UpdateRateAmountRequest;
import com.example.moneyflowapi.wallet.dto.request.UpdateWalletRequest;
import com.example.moneyflowapi.wallet.dto.response.WalletResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface WalletService {

    WalletResponse createWallet(CreateWalletRequest request);

    List<WalletResponse> getAll(Boolean isArchive);

    WalletResponse update(UUID id, UpdateWalletRequest request);

    void archiveWallet(UUID id);

    WalletResponse getOne(UUID id);

    Wallet findWalletForUpdate(UUID id, UUID ownerId);

    WalletResponse updateRateAmount(UUID userId, UUID id, UpdateRateAmountRequest request);
}