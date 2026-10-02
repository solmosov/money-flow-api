package com.example.moneyflowapi.role.seeder;

import com.example.moneyflowapi.common.seeder.DataSeeder;
import com.example.moneyflowapi.role.Role;
import com.example.moneyflowapi.role.RoleName;
import com.example.moneyflowapi.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleSeeder implements DataSeeder {

    private final RoleRepository roleRepo;

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    @Transactional
    public void seed() {
        if (roleRepo.count() > 0) {
            return;
        }

        List<Role> roles = List.of(
                Role.builder().name(RoleName.ADMIN).build(),
                Role.builder().name(RoleName.USER).build()
        );

        roleRepo.saveAll(roles);

        log.info("--------------------------------- Role seeded successfully ---------------------------------");
    }
}
