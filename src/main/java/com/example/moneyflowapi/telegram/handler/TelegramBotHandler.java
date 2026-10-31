package com.example.moneyflowapi.telegram.handler;

import com.example.moneyflowapi.telegram.dto.Message;
import com.example.moneyflowapi.telegram.dto.Update;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TelegramBotHandler {

    private static final String WEB_APP_URL = "https://dash.pulqadam.uz";
    private static final String WELCOME_PHOTO = "https://pulqadam.uz/assets/welcome.jpg";

    public record StartReply(
            Long chatId,
            String photoUrl,
            String html,
            String buttonText,
            String webAppUrl
    ) {}

    public Optional<StartReply> handle(Update update) {
        if (update == null || update.message() == null) {
            return Optional.empty();
        }

        Message message = update.message();

        if (message.chat() == null || !isStartCommand(message.text())) {
            return Optional.empty();
        }

        String languageCode = message.from() != null ? message.from().languageCode() : null;

        return Optional.of(buildStartReply(message.chat().id(), languageCode));
    }

    private boolean isStartCommand(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        String command = text.trim().split("\\s+")[0];
        int at = command.indexOf('@');
        if (at > 0) {
            command = command.substring(0, at);
        }
        return command.equalsIgnoreCase("/start");
    }

    private String resolveLang(String languageCode) {
        if (languageCode == null || languageCode.isBlank()) {
            return "en";
        }
        return languageCode.toLowerCase().split("-")[0];
    }

    private StartReply buildStartReply(Long chatId, String languageCode) {
        String lang = resolveLang(languageCode);

        String html = switch (lang) {
            case "uz" -> """
                    <b>👋 PulQadam'ga xush kelibsiz!</b>

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

        return new StartReply(chatId, WELCOME_PHOTO, html, buttonText, WEB_APP_URL);
    }
}