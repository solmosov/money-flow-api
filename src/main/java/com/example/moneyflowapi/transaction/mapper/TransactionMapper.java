package com.example.moneyflowapi.transaction.mapper;

import com.example.moneyflowapi.category.mapper.CategoryMapper;
import com.example.moneyflowapi.transaction.Transaction;
import com.example.moneyflowapi.transaction.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final CategoryMapper catMapper;

    public TransactionResponse toDto(Transaction transaction){
        return TransactionResponse.builder()
                .id(transaction.getId()).type(transaction.getType()).category(catMapper.toDto(transaction.getCategory())).amount(transaction.getAmount()).date(transaction.getDate()).description(transaction.getDescription()).createdAt(transaction.getCreatedAt()).updatedAt(transaction.getUpdatedAt()).build();
    }

    public List<TransactionResponse> toList(List<Transaction> transactions){
        return transactions.stream()
                .map(this::toDto)
                .toList();
    }
}
