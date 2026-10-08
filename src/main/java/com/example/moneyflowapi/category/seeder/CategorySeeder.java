package com.example.moneyflowapi.category.seeder;

import com.example.moneyflowapi.category.Category;
import com.example.moneyflowapi.category.CategoryRepository;
import com.example.moneyflowapi.category.CategoryType;
import com.example.moneyflowapi.common.seeder.DataSeeder;
import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class CategorySeeder implements DataSeeder {

    private final CategoryRepository categoryRepo;
    private final UserRepository userRepo;

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    @Transactional
    public void seed() {
        User user = userRepo.findByEmail("user@example.com")
                .orElseThrow(() ->
                        new IllegalStateException("User not found: user@example.com")
                );

        if (categoryRepo.countByUserAndDeletedAtIsNull(user) > 0) {
            return;
        }

        List<Category> categories = List.of(
                Category.builder()
                        .user(user)
                        .name("Food")
                        .type(CategoryType.EXPENSE)
                        .iconName("Utensils")
                        .build(),

                Category.builder()
                        .user(user)
                        .name("Transport")
                        .type(CategoryType.EXPENSE)
                        .iconName("Car")
                        .build(),

                Category.builder()
                        .user(user)
                        .name("Shopping")
                        .type(CategoryType.EXPENSE)
                        .iconName("ShoppingBag")
                        .build(),

                Category.builder()
                        .user(user)
                        .name("Salary")
                        .type(CategoryType.INCOME)
                        .iconName("Wallet")
                        .build(),

                Category.builder()
                        .user(user)
                        .name("Freelance")
                        .type(CategoryType.INCOME)
                        .iconName("Briefcase")
                        .build()
        );

        categoryRepo.saveAll(categories);

        log.info(
                "--------------------------------- Categories seeded successfully ({}) ---------------------------------",
                categories.size()
        );
    }
}