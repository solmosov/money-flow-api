package com.example.moneyflowapi.currency;

import com.example.moneyflowapi.currency.dto.response.CurrencyResponse;

import java.util.List;

public interface CurrencyService {

    List<CurrencyResponse> getActiveCurrencies();

    CurrencyResponse getByCode(String code);
}