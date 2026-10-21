package com.example.moneyflowapi.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    List<Account> findAllByUserIdAndArchivedAtIsNull(UUID userId);

    Optional<Account> findByIdAndUserIdAndArchivedAtIsNull(UUID id, UUID userId);
}