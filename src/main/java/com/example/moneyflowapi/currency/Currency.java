package com.example.moneyflowapi.currency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "currencies")
public class Currency {

    @Id
    @Column(name = "code", length = 3, nullable = false, updatable = false)
    private String code;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "symbol", length = 8, nullable = false)
    private String symbol;

    @Column(name = "decimals", nullable = false)
    private int decimals;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Currency other)) {
            return false;
        }
        return code != null && code.equals(other.getCode());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
