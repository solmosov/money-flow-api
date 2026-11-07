package com.example.moneyflowapi.telegram.bot;

import com.example.moneyflowapi.telegram.message.BotText;
import com.example.moneyflowapi.telegram.parser.ParsedTransaction;
import com.example.moneyflowapi.telegram.parser.TransactionMessageParser;
import com.example.moneyflowapi.telegram.resolver.BotLangResolver;
import com.example.moneyflowapi.transaction.TransactionType;
import com.example.moneyflowapi.user.UserLang;
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
    private final BotLangResolver langResolver;

    @CommandHandler("/start")
    public void start(BotContext context) {
        message.sendWelcomeMessage(context);
    }

    @MessageHandler
    public void anyMessage(BotContext context) {
        String text = context.message().text();

        if (text == null || text.startsWith("/")) {
            return;
        }

        UserLang lang = langResolver.resolve(context);
        Optional<ParsedTransaction> parsed = parser.parse(text);

        if (parsed.isEmpty()) {
            context.reply(BotText.PARSE_ERROR.get(lang)).send();
            return;
        }

        ParsedTransaction transaction = parsed.get();

        BotText typeText = transaction.type() == TransactionType.INCOME
                ? BotText.TYPE_INCOME
                : BotText.TYPE_EXPENSE;

        String reply = BotText.TRANSACTION_PARSED.get(
                lang,
                typeText.get(lang),
                transaction.category(),
                transaction.amount().stripTrailingZeros().toPlainString(),
                transaction.currency(),
                transaction.note() == null ? "-" : transaction.note()
        );

        context.reply(reply).send();
    }
}