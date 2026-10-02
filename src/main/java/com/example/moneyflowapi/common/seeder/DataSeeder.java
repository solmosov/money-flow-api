package com.example.moneyflowapi.common.seeder;

public interface DataSeeder {
    default int getOrder(){
        return Integer.MAX_VALUE;
    }

    void seed();
}
