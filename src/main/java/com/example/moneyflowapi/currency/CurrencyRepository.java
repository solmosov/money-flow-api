package com.example.moneyflowapi.currency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, CurrencyCode> {

    List<Currency> findAllByIsActiveTrueOrderByCodeAsc();

    List<Currency> findAllByIsActiveTrueAndIsPopularTrueOrderByCodeAsc();
}