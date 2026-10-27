package com.example.moneyflowapi.wallet;

import com.example.moneyflowapi.common.entity.BaseEntity;
import com.example.moneyflowapi.currency.Currency;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.goal.Goal;
import com.example.moneyflowapi.user.User;
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
@Table(name = "wallets")
public class Wallet extends BaseEntity {

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private WalletType type;


    @Column(name = "balance", precision = 14, scale = 2, nullable = false)
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_code", nullable = false)
    private Currency currency;

    @Column(name = "rate_amount", precision = 20, scale = 10, nullable = false)
    private BigDecimal rateAmount;

    @Column(name = "archived_at")
    private Instant archivedAt;

    public void balanceIncrement(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void balanceDecrement(BigDecimal amount){
        if(this.balance.compareTo(amount) < 0){
            throw new BusinessException(ErrorCode.USER_BALANCE_INSUFFICIENT);
        }

        this.balance = this.balance.subtract(amount);
    }
}
