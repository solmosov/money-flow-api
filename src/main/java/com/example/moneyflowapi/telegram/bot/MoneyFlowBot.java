package com.example.moneyflowapi.telegram.bot;

import io.github.solmosov.telegrambot.annotation.BotHandler;
import io.github.solmosov.telegrambot.annotation.CommandHandler;
import io.github.solmosov.telegrambot.context.BotContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@BotHandler("money_flow_bot")
@RequiredArgsConstructor
public class MoneyFlowBot {

    private final MoneyFlowBotMessage message;

    @CommandHandler("/start")
    public void start(BotContext context) {
        message.sendWelcomeMessage(context);
    }
}
