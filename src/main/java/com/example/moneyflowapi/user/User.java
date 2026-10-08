package com.example.moneyflowapi.user;

import com.example.moneyflowapi.common.entity.BaseEntity;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {

    @Column(name = "email", unique = true, length = 80)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "telegram_user_id", unique = true)
    private Long telegramUserId;

    @Column(name = "balance", precision = 14, scale = 2, nullable = false) // 100 000 000 000.00
    private BigDecimal balance;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "role_id", nullable = false)},
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"})
    )
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .toList();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

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
