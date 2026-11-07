package com.example.moneyflowapi.config;

import io.github.solmosov.telegrambot.bot.UpdatesMode;
import io.github.solmosov.telegrambot.spring.boot.TelegramBotRegistration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@Configuration
public class BotConfig {

    @Bean
    TelegramBotRegistration moneyFlowBotRegistration(
            Environment env,
            @Value("${app.telegram.bot-token}") String token,
            @Value("${app.telegram.webhook-url:}") String webhookUrl,
            @Value("${app.telegram.webhook-secret:}") String webhookSecret,
            @Value("${app.telegram.webhook-path-secret:}") String webhookPathSecret
    ) {
        var builder = TelegramBotRegistration.builder()
                .botName("money_flow_bot")
                .token(token);

        if (env.acceptsProfiles(Profiles.of("prod"))) {
            builder.config(config -> config
                    .updateMode(UpdatesMode.WEBHOOK)
                    .webhookUrl(webhookUrl)
                    .webhookSecret(webhookSecret)
                    .webhookPath("/webhook/telegram")
                    .webhookPathSecret(webhookPathSecret)
            );
        }

        return builder.build();
    }

}
