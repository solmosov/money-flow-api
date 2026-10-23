package com.example.moneyflowapi.wallet;

import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    List<Wallet> findAllByUserIdAndArchivedAtIsNull(UUID userId);

    Optional<Wallet> findByIdAndUserIdAndArchivedAtIsNull(UUID id, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Wallet a WHERE a.id = :id and a.user.id = :ownerId")
    Optional<Wallet> findByIdForUpdate(@NotNull UUID id, @Param("ownerId") UUID ownerId);
}