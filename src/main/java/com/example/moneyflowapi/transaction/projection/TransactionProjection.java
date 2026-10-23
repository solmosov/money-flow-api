package com.example.moneyflowapi.transaction.projection;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


public interface TransactionProjection {

    UUID getId();

    @Value("#{target.wallet_name}")
    String getWalletName();

    @Value("#{target.wallet_type}")
    String getWalletType();

    @Value("#{target.category_name}")
    String getCategoryName();

    @Value("#{target.category_icon_name}")
    String getCategoryIconName();

    BigDecimal getAmount();

    String getCurrencyCode();

    @Value("#{target.currency_symbol}")
    String getCurrencySymbol();

    String getDescription();

    String getType();

    Instant getDate();

    @Value("#{target.created_at}")
    Instant getCreatedAt();

    @Value("#{target.updated_at}")
    Instant getUpdatedAt();
}