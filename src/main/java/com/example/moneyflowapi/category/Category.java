package com.example.moneyflowapi.category;

import com.example.moneyflowapi.common.entity.BaseEntity;
import com.example.moneyflowapi.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(
        name = "categories",
        indexes = @Index(name = "idx_categories_user_id", columnList = "user_id")
)
public class Category extends BaseEntity {

    @OnDelete(action = OnDeleteAction.CASCADE)
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

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

}