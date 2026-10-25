package com.example.moneyflowapi.wallet;

import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
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
                    (:isArchive IS NULL AND w.archivedAt IS NULL)
                    OR (:isArchive = TRUE AND w.archivedAt IS NOT NULL)
                    OR (:isArchive = FALSE AND w.archivedAt IS NULL)
                )
           """)
    List<Wallet> findAllByOwner(
            @Param("userId") UUID userId,
            @Param("isArchive") Boolean isArchive
    );

    Optional<Wallet> findByIdAndUserIdAndArchivedAtIsNull(UUID id, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Wallet a WHERE a.id = :id and a.user.id = :ownerId")
    Optional<Wallet> findByIdForUpdate(@NotNull UUID id, @Param("ownerId") UUID ownerId);
}