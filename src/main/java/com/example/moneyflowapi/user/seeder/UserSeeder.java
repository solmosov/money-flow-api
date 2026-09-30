package com.example.moneyflowapi.user.seeder;

import com.example.moneyflowapi.common.seeder.DataSeeder;
import com.example.moneyflowapi.role.Role;
import com.example.moneyflowapi.role.RoleName;
import com.example.moneyflowapi.role.RoleRepository;
import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class UserSeeder implements DataSeeder {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    @Transactional
    public void seed() {
        if (userRepo.count() > 0) {
            return;
        }

        String encodedPassword = passwordEncoder.encode("secret");

        Map<RoleName, String[]> users = Map.of(
                RoleName.ADMIN, new String[]{"Admin", "admin@example.com"},
                RoleName.USER, new String[]{"User", "user@example.com"}
        );

        List<User> userList = users.entrySet().stream()
                .map(entry -> {
                    Role role = roleRepo.findByName(entry.getKey())
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Role not found: " + entry.getKey()
                                    )
                            );

                    return User.builder()
                            .email(entry.getValue()[1])
                            .password(encodedPassword)
                            .roles(Set.of(role))
                            .balance(BigDecimal.ZERO)
                            .build();
                })
                .toList();

        userRepo.saveAll(userList);

        log.info("--------------------------------- Users seeded successfully ({}) ---------------------------------", users.size());
    }
}