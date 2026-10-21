package com.example.moneyflowapi.goal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {

    List<Goal> findAllByUserIdOrderByDeadlineAsc(UUID userId);

    List<Goal> findAllByUserIdAndStatusOrderByDeadlineAsc(UUID userId, GoalStatus status);

    Optional<Goal> findByIdAndUserId(UUID id, UUID userId);
}