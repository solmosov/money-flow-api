package com.example.moneyflowapi.config;

import io.github.solmosov.telegrambot.spring.boot.TelegramBotRegistration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfig {

    @Value("${app.telegram.bot-token}")
    private String token;

    @Bean
    public TelegramBotRegistration moneyFlowBotRegistration(){
        return TelegramBotRegistration.builder()
                .botName("money_flow_bot")
                .token(token)
                .build();
    }
}
