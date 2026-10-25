package com.example.moneyflowapi.goal;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {

    @EntityGraph(attributePaths = {"wallet"})
    List<Goal> findAllByUserIdOrderByDeadlineAsc(UUID userId);

    @EntityGraph(attributePaths = {"wallet"})
    List<Goal> findAllByUserIdAndStatusOrderByDeadlineAsc(UUID userId, GoalStatus status);

    @EntityGraph(attributePaths = {"wallet"})
    Optional<Goal> findByIdAndUserId(UUID id, UUID userId);
}