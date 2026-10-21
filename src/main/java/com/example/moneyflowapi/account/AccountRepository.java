package com.example.moneyflowapi.account;

import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    List<Account> findAllByUserIdAndArchivedAtIsNull(UUID userId);

    Optional<Account> findByIdAndUserIdAndArchivedAtIsNull(UUID id, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id and a.user.id = :ownerId")
    Optional<Account> findByIdForUpdate(@NotNull UUID uuid, @Param("ownerId") UUID ownerId);
}