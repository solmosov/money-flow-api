package com.example.moneyflowapi.transaction.mapper;

import com.example.moneyflowapi.category.mapper.CategoryMapper;
import com.example.moneyflowapi.currency.mapper.CurrencyMapper;
import com.example.moneyflowapi.transaction.Transaction;
import com.example.moneyflowapi.transaction.dto.response.TransactionFeedResponse;
import com.example.moneyflowapi.transaction.dto.response.TransactionResponse;
import com.example.moneyflowapi.wallet.mapper.WalletMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final CategoryMapper catMapper;
    private final WalletMapper walletMapper;
    private final CategoryMapper categoryMapper;
    private final CurrencyMapper currencyMapper;

    public TransactionResponse toDto(Transaction transaction){
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .category(catMapper.toDto(transaction.getCategory()))
                .amount(transaction.getAmount())
                .rateAmount(transaction.getAmount())
                .date(transaction.getDate())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    public TransactionFeedResponse toDtoFeed(Transaction transaction) {
        return TransactionFeedResponse.builder()
                .id(transaction.getId())
                .wallet(walletMapper.toDto(transaction.getWallet()))
                .category(categoryMapper.toDto(transaction.getCategory()))
                .currency(currencyMapper.toDto(transaction.getCurrency()))
                .amount(transaction.getAmount())
                .rateAmount(transaction.getRateAmount())
                .description(transaction.getDescription())
                .type(transaction.getType())
                .date(transaction.getDate())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    public List<TransactionResponse> toList(List<Transaction> transactions){
        return transactions.stream()
                .map(this::toDto)
                .toList();
    }

    public Page<TransactionResponse> toPage(Page<Transaction> all) {
        return all.map(this::toDto);
    }
}
