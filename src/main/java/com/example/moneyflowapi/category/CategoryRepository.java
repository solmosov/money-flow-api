package com.example.moneyflowapi.category;

import com.example.moneyflowapi.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    double countByUserAndDeletedAtIsNull(User user);

    boolean existsByIdAndTypeAndDeletedAtIsNull(UUID categoryId, CategoryType categoryType);

    @Query("""
            SELECT c
            FROM Category c
            WHERE c.user.id = :userId
                AND c.deletedAt IS NULL
                AND (:type IS NULL OR c.type = :type)
            ORDER BY c.createdAt
            """)
    Page<Category> findAllByUserId(
            @Param("userId") UUID userId,
            @Param("type") CategoryType type,
            Pageable pageable
    );

    Optional<Category> findByIdAndUserIdAndDeletedAtIsNull(UUID categoryId, UUID userId);

    @Modifying(clearAutomatically = true)
    @Query("""
                UPDATE Category c
                SET c.deletedAt = :deletedAt
                WHERE c.id = :id
                    AND c.user.id = :userId
                    AND c.deletedAt IS NULL
            """)
    int softDeleteByIdAndUserId(
            @Param("id") UUID id,
            @Param("userId") UUID userId,
            @Param("deletedAt") Instant deletedAt
    );
}
