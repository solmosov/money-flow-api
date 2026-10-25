package com.example.moneyflowapi.goal.mapper;

import com.example.moneyflowapi.goal.Goal;
import com.example.moneyflowapi.goal.dto.response.GoalResponse;
import com.example.moneyflowapi.wallet.mapper.WalletMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoalMapper {

    private final WalletMapper walletMapper;

    public GoalResponse toDto(Goal goal) {
        return GoalResponse.builder()
                .id(goal.getId())
                .name(goal.getName())
                .targetAmount(goal.getTargetAmount())
                .currencyCode(goal.getCurrency().getCode())
                .rateSnapshot(goal.getRateSnapshot())
                .baseAmountSnapshot(goal.getBaseAmountSnapshot())
                .startingAmount(goal.getStartingAmount())
                .deadline(goal.getDeadline())
                .status(goal.getStatus())
                .completedAt(goal.getCompletedAt())
                .createdAt(goal.getCreatedAt())
                .updateAt(goal.getUpdatedAt())
                .wallet(walletMapper.toDto(goal.getWallet()))
                .build();
    }
}