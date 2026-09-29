package com.example.moneyflowapi.category;

import com.example.moneyflowapi.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    double countByUser(User user);

    boolean existsByIdAndType(UUID categoryId, CategoryType categoryType);
}
