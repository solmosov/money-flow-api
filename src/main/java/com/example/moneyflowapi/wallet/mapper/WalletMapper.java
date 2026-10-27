package com.example.moneyflowapi.wallet.mapper;

import com.example.moneyflowapi.wallet.Wallet;
import com.example.moneyflowapi.wallet.dto.response.WalletResponse;
import com.example.moneyflowapi.currency.mapper.CurrencyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletMapper {

    private final CurrencyMapper currencyMapper;

    public WalletResponse toDto(Wallet wallet){
        return WalletResponse.builder()
                .id(wallet.getId())
                .name(wallet.getName())
                .type(wallet.getType())
                .currency(currencyMapper.toDto(wallet.getCurrency()))
                .balance(wallet.getBalance())
                .rateAmount(wallet.getRateAmount())
                .archivedAt(wallet.getArchivedAt())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }
}
