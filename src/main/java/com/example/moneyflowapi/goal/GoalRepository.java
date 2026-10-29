package com.example.moneyflowapi.goal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {

    @EntityGraph(attributePaths = {"wallet.currency"})
    Page<Goal> findAllByUserIdAndStatus(UUID userId, GoalStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"wallet.currency"})
    Page<Goal> findAllByUserIdAndStatusNot(UUID userId, GoalStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"wallet.currency"})
    Page<Goal> findAllByUserId(UUID userId, Pageable pageable);

    @EntityGraph(attributePaths = {"wallet.currency"})
    Optional<Goal> findByIdAndUserId(UUID id, UUID userId);
}