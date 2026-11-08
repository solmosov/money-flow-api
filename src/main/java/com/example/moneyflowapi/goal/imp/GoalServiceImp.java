package com.example.moneyflowapi.goal.imp;

import com.example.moneyflowapi.common.response.PageResponse;
import com.example.moneyflowapi.currency.Currency;
import com.example.moneyflowapi.currency.CurrencyRepository;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.goal.Goal;
import com.example.moneyflowapi.goal.GoalRepository;
import com.example.moneyflowapi.goal.GoalService;
import com.example.moneyflowapi.goal.GoalStatus;
import com.example.moneyflowapi.goal.dto.request.CreateGoalRequest;
import com.example.moneyflowapi.goal.dto.request.UpdateGoalRequest;
import com.example.moneyflowapi.goal.dto.response.GoalResponse;
import com.example.moneyflowapi.goal.mapper.GoalMapper;
import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.user.UserRepository;
import com.example.moneyflowapi.wallet.Wallet;
import com.example.moneyflowapi.wallet.WalletRepository;
import com.example.moneyflowapi.wallet.WalletType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoalServiceImp implements GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper mapper;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public GoalResponse createGoal(CreateGoalRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Currency currency = currencyRepository.findById(request.currencyCode())
                .filter(Currency::isActive)
                .orElseThrow(() -> new BusinessException(ErrorCode.CURRENCY_NOT_FOUND));

        BigDecimal rate = request.rateAmount();

        Wallet newGoalWallet = Wallet.builder()
                .user(userRepository.getReferenceById(userId))
                .name(request.name())
                .type(WalletType.GOAL)
                .balance(request.startingAmount())
                .currency(currency)
                .rateAmount(rate)
                .build();

        Goal goal = Goal.builder()
                .user(userRepository.getReferenceById(userId))
                .wallet(newGoalWallet)
                .name(request.name())
                .targetAmount(request.targetAmount())
                .currency(currency)
                .rateSnapshot(rate)
                .baseAmountSnapshot(toBaseAmount(request.targetAmount(), rate))
                .startingAmount(request.startingAmount())
                .deadline(request.deadline())
                .status(GoalStatus.ACTIVE)
                .build();

        walletRepository.save(newGoalWallet);

        var saved = goalRepository.save(goal);
        return mapper.toDto(findOwnedGoal(saved.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<GoalResponse> myGoals(GoalStatus status, Pageable pageable) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Page<Goal> result;
        if (status == null) {
            Pageable p = withSort(pageable, Sort.by(Sort.Order.asc("deadline"), Sort.Order.asc("id")));
            result = goalRepository.findAllByUserId(userId, p);
        } else if (status == GoalStatus.COMPLETED) {
            Pageable p = withSort(pageable, Sort.by(Sort.Order.desc("completedAt"), Sort.Order.asc("id")));
            result = goalRepository.findAllByUserIdAndStatus(userId, GoalStatus.COMPLETED, p);
        } else {
            Pageable p = withSort(pageable, Sort.by(Sort.Order.asc("deadline"), Sort.Order.asc("id")));
            result = goalRepository.findAllByUserIdAndStatusNot(userId, GoalStatus.COMPLETED, p);
        }

        return PageResponse.<GoalResponse>builder()
                .content(result.getContent().stream().map(mapper::toDto).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();
    }

    private Pageable withSort(Pageable pageable, Sort sort) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    @Override
    @Transactional(readOnly = true)
    public GoalResponse getGoal(UUID id) {
        return mapper.toDto(findOwnedGoal(id));
    }

    @Override
    @Transactional
    public GoalResponse updateGoal(UUID id, UpdateGoalRequest request) {
        Goal goal = findOwnedGoal(id);
        ensureActive(goal);

        goal.setName(request.name());
        goal.setTargetAmount(request.targetAmount());
        goal.setBaseAmountSnapshot(toBaseAmount(request.targetAmount(), goal.getRateSnapshot()));
        goal.setDeadline(request.deadline());

        return mapper.toDto(goal);
    }

    @Override
    @Transactional
    public GoalResponse completeGoal(UUID id) {
        Goal goal = findOwnedGoal(id);
        ensureActive(goal);

        goal.setStatus(GoalStatus.COMPLETED);
        goal.setCompletedAt(Instant.now());

        return mapper.toDto(goal);
    }

    private Goal findOwnedGoal(UUID id) {
        UUID userId = SecurityUtil.getCurrentUserId();

        return goalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GOAL_NOT_FOUND));
    }

    private void ensureActive(Goal goal) {
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.GOAL_ALREADY_COMPLETED);
        }
    }

    private BigDecimal toBaseAmount(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}