package com.example.moneyflowapi.user;

import com.example.moneyflowapi.user.dto.response.BalanceResponse;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
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

    Optional<User> findByEmail(String mail);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdForUpdate(@Param("id") UUID id);

    @Query("""
                SELECT u.balance
                FROM User u
                WHERE u.id = :id
            """)
    Optional<BalanceResponse> findBalanceById(@Param("id") UUID id);

    Optional<User> findByTelegramUserId(long telegramUserId);
}
