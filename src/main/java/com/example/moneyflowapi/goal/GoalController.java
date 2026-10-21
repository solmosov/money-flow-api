package com.example.moneyflowapi.goal;

import com.example.moneyflowapi.goal.dto.request.CreateGoalRequest;
import com.example.moneyflowapi.goal.dto.request.UpdateGoalRequest;
import com.example.moneyflowapi.goal.dto.response.GoalResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalResponse createGoal(@Valid @RequestBody CreateGoalRequest request) {
        return goalService.createGoal(request);
    }

    @GetMapping
    public List<GoalResponse> myGoals(@RequestParam(required = false) GoalStatus status) {
        return goalService.myGoals(status);
    }

    @GetMapping("/{id}")
    public GoalResponse getGoal(@PathVariable UUID id) {
        return goalService.getGoal(id);
    }

    @PutMapping("/{id}")
    public GoalResponse updateGoal(@PathVariable UUID id,
                                   @Valid @RequestBody UpdateGoalRequest request) {
        return goalService.updateGoal(id, request);
    }

    @PatchMapping("/{id}/complete")
    public GoalResponse completeGoal(@PathVariable UUID id) {
        return goalService.completeGoal(id);
    }
}