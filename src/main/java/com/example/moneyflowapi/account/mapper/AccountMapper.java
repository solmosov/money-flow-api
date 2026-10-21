package com.example.moneyflowapi.account.mapper;

import com.example.moneyflowapi.account.Account;
import com.example.moneyflowapi.account.dto.response.AccountResponse;
import com.example.moneyflowapi.currency.mapper.CurrencyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapper {

    private final CurrencyMapper currencyMapper;

    public AccountResponse toDto(Account account){
        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .type(account.getType())
                .currency(currencyMapper.toDto(account.getCurrency()))
                .balance(account.getBalance())
                .archivedAt(account.getArchivedAt())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
