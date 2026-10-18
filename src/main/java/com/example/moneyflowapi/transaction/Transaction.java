package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.category.Category;
import com.example.moneyflowapi.common.entity.BaseEntity;
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
        name = "transactions",
        indexes = {
                @Index(name = "idx_transactions_user_date", columnList = "user_id, date DESC"),
                @Index(name = "idx_transactions_user_category_date", columnList = "user_id, category_id, date DESC"),
                @Index(name = "idx_transactions_category_id", columnList = "category_id")
        }
)
public class Transaction extends BaseEntity {

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @Column(name = "amount",  precision = 14, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private TransactionCurrency currency;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "description")
    private String description;
}
