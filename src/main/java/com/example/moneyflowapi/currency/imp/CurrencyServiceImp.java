package com.example.moneyflowapi.currency.imp;

import com.example.moneyflowapi.currency.CurrencyCode;
import com.example.moneyflowapi.currency.CurrencyRepository;
import com.example.moneyflowapi.currency.CurrencyService;
import com.example.moneyflowapi.currency.dto.response.CurrencyResponse;
import com.example.moneyflowapi.currency.mapper.CurrencyMapper;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurrencyServiceImp implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper mapper;
    @Override
    public List<CurrencyResponse> getActiveCurrencies() {
        return currencyRepository.findAllByIsActiveTrueOrderByCodeAsc()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public CurrencyResponse getByCode(CurrencyCode code) {
        return currencyRepository.findById(code)
                .filter(currency -> currency.isActive())
                .map(mapper::toDto)
                .orElseThrow(() -> new BusinessException(ErrorCode.CURRENCY_NOT_FOUND));
    }
}