
package com.example.moneyflowapi.telegram.moneyFlowBot;

import io.github.solmosov.telegrambot.annotation.BotHandler;
import io.github.solmosov.telegrambot.annotation.CommandHandler;
import io.github.solmosov.telegrambot.context.BotContext;
import io.github.solmosov.telegrambot.keyboard.inline.InlineKeyboard;
import io.github.solmosov.telegrambot.keyboard.inline.InlineKeyboardButton;
import org.springframework.stereotype.Component;

@Component
@BotHandler("money_flow_bot")
public class MoneyFlowBot {

    @CommandHandler("/start")
    public void start(BotContext context) {
        String langCode = context.message().from().languageCode();

        String lang = langCode == null ? "en" : langCode.toLowerCase();

        String html = switch (lang) {
            case "uz" -> """
                    <b>👋 PulQadam'ga xuş kelibsiz!</b>

                    Pul oqimingizni nazorat qiling va moliyaviy qarorlaringizni ongli ravishda qabul qiling.

                    <b>✨ PulQadam imkoniyatlari:</b>
                    💰 Daromad va xarajatlarni qayd etish
                    📊 Pul oqimini tahlil qilish
                    📁 PDF va CSV hisobotlarini yuklab olish

                    <b>🚀 Moliyaviy nazoratni bugun boshlang!</b>
                    """;
            case "ru" -> """
                    <b>👋 Добро пожаловать в PulQadam!</b>

                    Контролируйте свои деньги и принимайте осознанные финансовые решения.

                    <b>✨ Возможности PulQadam:</b>
                    💰 Учёт доходов и расходов
                    📊 Анализ денежных потоков
                    📁 Экспорт отчётов в PDF и CSV

                    <b>🚀 Начните управлять своими финансами уже сегодня!</b>
                    """;
            default -> """
                    <b>👋 Welcome to PulQadam!</b>

                    Take control of your money and make smarter financial decisions.

                    <b>✨ What you can do with PulQadam:</b>
                    💰 Track income and expenses
                    📊 Analyze your money flow
                    📁 Export reports in PDF and CSV

                    <b>🚀 Start managing your money today!</b>
                    """;
        };

        String buttonText = switch (lang) {
            case "uz" -> "📱 Ilovani ochish";
            case "ru" -> "📱 Открыть приложение";
            default -> "📱 Open App";
        };

        var keyboard = InlineKeyboard.of(
                InlineKeyboard.buttonWebApp(
                        buttonText,
                        "https://dash.pulqadam.uz",
                        InlineKeyboardButton.Style.PRIMARY
                )
        );

        context.photo("https://pulqadam.uz/assets/welcome.jpg")
                .caption(html)
                .options(options -> options.html())
                .keyboard(keyboard)
                .send();
    }
}
