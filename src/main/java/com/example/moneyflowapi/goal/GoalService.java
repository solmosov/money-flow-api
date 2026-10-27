package com.example.moneyflowapi.goal;

import com.example.moneyflowapi.goal.dto.request.CreateGoalRequest;
import com.example.moneyflowapi.goal.dto.request.UpdateGoalRequest;
import com.example.moneyflowapi.goal.dto.response.GoalResponse;

import java.util.List;
import java.util.UUID;

public interface GoalService {

    GoalResponse createGoal(CreateGoalRequest request);

    List<GoalResponse> myGoals(GoalStatus status);

    GoalResponse getGoal(UUID id);

    GoalResponse updateGoal(UUID id, UpdateGoalRequest request);

    GoalResponse completeGoal(UUID id);
}