package com.example.moneyflowapi.currency;

import com.example.moneyflowapi.currency.dto.response.CurrencyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    public List<CurrencyResponse> getActiveCurrencies() {
        return currencyService.getActiveCurrencies();
    }

    @GetMapping("/{code}")
    public CurrencyResponse getByCode(@PathVariable String code) {
        return currencyService.getByCode(code);
    }
}