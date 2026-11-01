package com.example.moneyflowapi.user;

import com.example.moneyflowapi.user.dto.response.BalanceResponse;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("""
            SELECT DISTINCT u
            FROM User u
            JOIN FETCH u.roles
            WHERE LOWER(u.email) = LOWER(:email)
            """)
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    @Query("""
            SELECT DISTINCT u
            FROM User u
            JOIN FETCH u.roles
            WHERE u.telegramUserId = :telegramUserId
            """)
    Optional<User> findByTelegramUserIdWithRoles(@Param("telegramUserId") Long telegramUserId);

    Optional<User> findByEmail(String mail);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdForUpdate(@Param("id") UUID id);

    @Query("""
            SELECT 
                COALESCE(SUM(w.balance * w.rateAmount), 0),
                cr,
                CASE WHEN COUNT(CASE WHEN w.isRateChangeConfirmed = FALSE THEN 1 END) = 0
                     THEN TRUE ELSE FALSE END
            FROM User u
            LEFT JOIN u.baseCurrency cr
            LEFT JOIN Wallet w ON w.user = u
            WHERE u.id = :id
            GROUP BY u.id, u.baseCurrency
            """)
    Optional<BalanceResponse> findBalanceById(@Param("id") UUID id);

    @Modifying
    @Query("DELETE FROM User u WHERE u.id = :userId")
    int deleteByUserId(@Param("userId") UUID userId);

    @Query("SELECT u.telegramUserId FROM User u WHERE u.id = :userId")
    Optional<Long> findTelegramUserIdById(UUID userId);

    @Query("select u.id from User u where u.email = :email")
    Optional<UUID> findIdByEmail(@Param("email") String email);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE users SET is_demo_usage = false WHERE id = :id", nativeQuery = true)
    int disableDemoUsage(@Param("id") UUID id);

    @Modifying
    @Query(value = """
            UPDATE users u
            SET
                first_transaction_at = (
                    SELECT t.created_at
                    FROM transactions t
                    WHERE t.user_id = u.id
                    ORDER BY t.created_at ASC
                    LIMIT 1
                ),
                third_transaction_at = (
                    SELECT t.created_at
                    FROM transactions t
                    WHERE t.user_id = u.id
                    ORDER BY t.created_at ASC
                    OFFSET 2
                    LIMIT 1
                )
            WHERE u.id = :userId
            """, nativeQuery = true)
    void updateTransactionTimestamps(@Param("userId") UUID userId);
}
