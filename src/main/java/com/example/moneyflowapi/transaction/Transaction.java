package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.wallet.Wallet;
import com.example.moneyflowapi.category.Category;
import com.example.moneyflowapi.common.entity.BaseEntity;
import com.example.moneyflowapi.currency.Currency;
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
@Table(
        name = "transactions"
)
public class Transaction extends BaseEntity {

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false, updatable = false)
    private Wallet wallet;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_wallet_id")
    private Wallet toWallet;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @Column(name = "amount",  precision = 14, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "to_amount",  precision = 14, scale = 2)
    private BigDecimal toAmount;

    @Column(name = "rate_amount", precision = 8, scale = 2, nullable = false)
    private BigDecimal rateAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_code", nullable = false)
    private Currency currency;

    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "description")
    private String description;
}
