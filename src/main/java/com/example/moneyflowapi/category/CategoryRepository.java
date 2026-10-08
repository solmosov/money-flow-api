package com.example.moneyflowapi.category;

import com.example.moneyflowapi.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    double countByUser(User user);

    boolean existsByIdAndType(UUID categoryId, CategoryType categoryType);

    @Query("""
            SELECT c
            FROM Category c
            WHERE c.user.id = :userId
                AND (:type IS NULL OR c.type = :type)
            ORDER BY c.name
            """)
    Page<Category> findAllByUserId(
            @Param("userId") UUID userId,
            @Param("type") CategoryType type,
            Pageable pageable
    );

    Optional<Category> findByIdAndUserId(UUID categoryId, UUID userId);

    int deleteByIdAndUserId(UUID categoryId, UUID userId);
}
