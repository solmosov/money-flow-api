package com.example.moneyflowapi.goal;

import com.example.moneyflowapi.common.response.PageResponse;
import com.example.moneyflowapi.goal.dto.request.CreateGoalRequest;
import com.example.moneyflowapi.goal.dto.request.UpdateGoalRequest;
import com.example.moneyflowapi.goal.dto.response.GoalResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface GoalService {

    GoalResponse createGoal(CreateGoalRequest request);

    PageResponse<GoalResponse> myGoals(GoalStatus status, Pageable pageable);

    GoalResponse getGoal(UUID id);

    GoalResponse updateGoal(UUID id, UpdateGoalRequest request);

    GoalResponse completeGoal(UUID id);
}