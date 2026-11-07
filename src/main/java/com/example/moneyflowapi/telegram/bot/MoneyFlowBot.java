package com.example.moneyflowapi.telegram.bot;

import com.example.moneyflowapi.telegram.parser.ParsedTransaction;
import com.example.moneyflowapi.telegram.parser.TransactionMessageParser;
import io.github.solmosov.telegrambot.annotation.BotHandler;
import io.github.solmosov.telegrambot.annotation.CommandHandler;
import io.github.solmosov.telegrambot.annotation.MessageHandler;
import io.github.solmosov.telegrambot.context.BotContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@BotHandler("money_flow_bot")
@RequiredArgsConstructor
public class MoneyFlowBot {

    private final MoneyFlowBotMessage message;
    private final TransactionMessageParser parser;

    @CommandHandler("/start")
    public void start(BotContext context) {
        message.sendWelcomeMessage(context);
    }

    @MessageHandler
    public void anyMessage(BotContext context) {
        String text = context.message().message().text();

        if (text == null || text.startsWith("/")) {
            return;
        }

        Optional<ParsedTransaction> parsed = parser.parse(text);

        if (parsed.isEmpty()) {
            context.reply("""
                    Could not understand the message.

                    Format:
                    Category Amount [Currency] [Note]
                    +Category Amount [Currency] [Note]

                    Examples:
                    Taxi 30000
                    Lunch 5 usd with friends
                    +Salary 5000000 uzs""").send();
            return;
        }

        ParsedTransaction transaction = parsed.get();

        String reply = """
                Type: %s
                Category: %s
                Amount: %s %s
                Note: %s""".formatted(
                transaction.type(),
                transaction.category(),
                transaction.amount().stripTrailingZeros().toPlainString(),
                transaction.currency(),
                transaction.note() == null ? "-" : transaction.note()
        );

        context.reply(reply).send();
    }
}