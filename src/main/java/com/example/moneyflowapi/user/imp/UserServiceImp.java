package com.example.moneyflowapi.user.imp;

import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.role.Role;
import com.example.moneyflowapi.role.RoleName;
import com.example.moneyflowapi.role.RoleRepository;
import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.user.UserRepository;
import com.example.moneyflowapi.user.UserService;
import com.example.moneyflowapi.user.dto.response.AuthUserResponse;
import com.example.moneyflowapi.user.dto.response.BalanceResponse;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmailWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    @Override
    public UserDetails loadUserByTelegramUserId(Long telegramUserId) throws UsernameNotFoundException {
        return userRepo.findByTelegramUserIdWithRoles(telegramUserId)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    @Override
    public BalanceResponse getBalance(UUID currentUserId) {
        return userRepo.findBalanceById(currentUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public AuthUserResponse getMe() {
        User user = userRepo.findById(SecurityUtil.getCurrentUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        return AuthUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .balance(user.getBalance())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .build();
    }

    @Override
    @Transactional
    public User registerUserByTelegram(long telegramUserId) {
        return userRepo.findByTelegramUserIdWithRoles(telegramUserId)
                .orElseGet(() -> {
                    Role role = roleRepo.findByName(RoleName.USER)
                            .orElseThrow(() ->
                                    new IllegalStateException("Role not found: USER")
                            );

                    return userRepo.save(User.builder()
                            .email(null)
                            .password(null)
                            .telegramUserId(telegramUserId)
                            .roles(Set.of(role))
                            .balance(BigDecimal.ZERO)
                            .build());
                });
    }

    @Transactional
    @Override
    public void deleteAccount(UUID userId) {
        int deleted = userRepo.deleteByUserId(userId);
        if (deleted == 0) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
    }
}
