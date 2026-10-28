package com.example.moneyflowapi.wallet;

import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @EntityGraph(attributePaths = {"currency"})
    @Query("""
           SELECT w
           FROM Wallet w
           WHERE w.user.id = :userId
                AND (
                    (:isArchive = TRUE AND w.archivedAt IS NOT NULL)
                    OR (COALESCE(:isArchive, FALSE) = FALSE AND w.archivedAt IS NULL)
                )
           """)
    List<Wallet> findAllByOwner(
            @Param("userId") UUID userId,
            @Param("isArchive") Boolean isArchive
    );

    Optional<Wallet> findByIdAndUserIdAndArchivedAtIsNull(UUID id, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :id and w.user.id = :ownerId")
    Optional<Wallet> findByIdForUpdate(@NotNull UUID id, @Param("ownerId") UUID ownerId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
       UPDATE Wallet w
       SET w.isRateChangeConfirmed = FALSE
       WHERE w.user.id = :userId
            AND w.isRateChangeConfirmed = TRUE
       """)
    int markAllRateChangeUnconfirmedByUserId(@Param("userId") UUID userId);
}