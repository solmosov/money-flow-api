package com.example.moneyflowapi.category;

import com.example.moneyflowapi.common.entity.BaseEntity;
import com.example.moneyflowapi.user.User;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_categories_user_id_name_type", columnNames = {"user_id", "name", "type"})
        }
)
public class Category extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CategoryType type;

    @Column(name = "icon_name", nullable = false, length = 20)
    private String iconName;
}