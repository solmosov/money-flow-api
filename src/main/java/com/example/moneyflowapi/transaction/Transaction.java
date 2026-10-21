package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.account.Account;
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
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private Account account;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @Column(name = "amount",  precision = 14, scale = 2, nullable = false)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_code", nullable = false)
    private Currency currency;

    @Column(name = "charged_amount",  precision = 14, scale = 2, nullable = false)
    private BigDecimal chargedAmount;

    @Column(name = "rate_to_base",  precision = 8, scale = 2, nullable = false)
    private BigDecimal rateToBase;

    @Column(name = "base_amount",  precision = 14, scale = 2, nullable = false)
    private BigDecimal baseAmount; // base currency amount

    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "description")
    private String description;

    @Column(name = "transfer_id", nullable = false, updatable = false, length = 40)
    private String transferId;
}
