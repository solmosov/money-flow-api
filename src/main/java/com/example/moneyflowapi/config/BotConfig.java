package com.example.moneyflowapi.config;

import io.github.solmosov.telegrambot.spring.boot.TelegramBotRegistration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Value("${app.telegram.bot-token}")
    private String moneyFlowBotToken;

    @Bean
    TelegramBotRegistration moneyFlowBotRegistration(){
        return TelegramBotRegistration.builder()
                .botName("money_flow_bot")
                .token(moneyFlowBotToken)
                .build();
    }
}
