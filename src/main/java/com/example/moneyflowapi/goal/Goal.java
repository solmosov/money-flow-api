package com.example.moneyflowapi.goal;

import com.example.moneyflowapi.common.entity.BaseEntity;
import com.example.moneyflowapi.currency.Currency;
import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.wallet.Wallet;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "goals")
public class Goal extends BaseEntity {

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false, updatable = false)
    private Wallet wallet;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Column(name = "target_amount", precision = 14, scale = 2, nullable = false)
    private BigDecimal targetAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_code", nullable = false)
    private Currency currency;

    @Column(name = "rate_snapshot", precision = 8, scale = 2, nullable = false)
    private BigDecimal rateSnapshot;

    @Column(name = "base_amount", precision = 14, scale = 2, nullable = false)
    private BigDecimal baseAmountSnapshot;

    @Column(name = "starting_amount", precision = 14, scale = 2, nullable = false)
    private BigDecimal startingAmount;

    @Column(name = "deadline", nullable = false)
    private Instant deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 14)
    private GoalStatus status;

    @Column(name = "completed_at")
    private Instant completedAt;

    public boolean isCompleted(){
        return completedAt != null;
    }
}
