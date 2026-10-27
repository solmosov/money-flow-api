package com.example.moneyflowapi.currency.mapper;

import com.example.moneyflowapi.currency.Currency;
import com.example.moneyflowapi.currency.dto.response.CurrencyResponse;
import org.springframework.stereotype.Component;

@Component
public class CurrencyMapper {

    public CurrencyResponse toDto(Currency currency) {
        return CurrencyResponse.builder()
                .code(currency.getCode()).name(currency.getName()).symbol(currency.getSymbol()).decimal(currency.getDecimals()).isActive(currency.isActive())
                .build();
    }
}
