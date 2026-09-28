package com.example.moneyflowapi.common.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MainSeeder implements CommandLineRunner {

    private final List<DataSeeder> seeders;

    @Override
    public void run(String... args) throws Exception {
        if (seeders.isEmpty()) {
            return;
        }

        List<DataSeeder> sortedSeeders = seeders.stream()
                .sorted(Comparator.comparingInt(DataSeeder::getOrder))
                .toList();

        for(DataSeeder seeder : sortedSeeders){
            seeder.seed();
        }
    }
}
