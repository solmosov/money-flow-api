package com.example.moneyflowapi.telegram.bot;

import com.example.moneyflowapi.telegram.resolver.BotLangResolver;
import com.example.moneyflowapi.user.UserLang;
import io.github.solmosov.telegrambot.context.BotContext;
import io.github.solmosov.telegrambot.keyboard.inline.InlineKeyboard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotStartMessage {

    private static final String WEB_APP_URL = "https://dash.pulqadam.uz";
    private static final String WELCOME_PHOTO = "https://pulqadam.uz/assets/welcome.jpg";
    private static final String OPEN_APP_BUTTON = "📱 Open App";


    private final BotOnboardingMessage onboardingMessage;
    private final BotLangResolver langResolver;

    public void sendWelcomeMessage(BotContext context) {
        UserLang lang = langResolver.resolve(context);

        var keyboard = InlineKeyboard.of(
                InlineKeyboard.buttonWebApp(openAppButton(lang), WEB_APP_URL)
        );

        context.photo(WELCOME_PHOTO)
                .keyboard(keyboard)
                .caption(welcomeHtml(lang))
                .options(options -> options.html())
                .send();
    }

    private String openAppButton(UserLang lang) {
        return switch (lang) {
            case UZ -> "📱 Ilovani ochish";
            case RU -> "📱 Открыть приложение";
            case EN -> "📱 Open App";
        };
    }

    private String welcomeHtml(UserLang lang) {
        return switch (lang) {
            case UZ -> """
                    <b>👋 PulQadam'ga xush kelibsiz!</b>

                    PulQadam — bu shunchaki daromad va xarajatlarni qayd etish ilovasi emas. Bu moliyaviy holatingizni tushunish, ongli qarorlar qabul qilish va maqsadlaringizga erishish yo'lidagi yordamchingiz.

                    <b>🌳 PulQadam bilan siz:</b>
                    - 💰 Daromad va xarajatlaringizni nazorat qilasiz
                    - 📊 Moliyaviy holatingizni tahlil qilasiz va oldingi davrlar bilan taqqoslaysiz
                    - 🧠 Raqamlarga asoslangan oqilona qarorlar qabul qilasiz
                    - 🎯 Jamg'arma maqsadlaringizni belgilaysiz va ularga intilasiz
                    - 📈 Moliyaviy o'sishingizni kuzatib borasiz
                    - 📁 Hisobotlarni PDF va CSV formatida yuklab olasiz

                    🔒 PulQadam foydalanuvchi ma'lumotlaridan faqat Telegram ID'ni saqlaydi.

                    <b>🚀 Moliyaviy maqsadlaringiz sari birinchi qadamingizni bugun boshlang!</b>

                    🌐 pulqadam.uz
                    """;
            case RU -> """
                    <b>👋 Добро пожаловать в PulQadam!</b>

                    PulQadam — это не просто приложение для учёта доходов и расходов. Это ваш помощник в понимании финансового положения, принятии осознанных решений и достижении финансовых целей.

                    <b>🌳 С PulQadam вы сможете:</b>
                    - 💰 Контролировать доходы и расходы
                    - 📊 Анализировать финансы и сравнивать периоды
                    - 🧠 Принимать обоснованные финансовые решения
                    - 🎯 Ставить цели накопления и двигаться к ним
                    - 📈 Отслеживать свой финансовый прогресс
                    - 📁 Скачивать отчёты в PDF и CSV

                    🔒 PulQadam хранит из пользовательских данных только Telegram ID.

                    <b>🚀 Сделайте первый шаг к своим финансовым целям уже сегодня!</b>

                    🌐 pulqadam.uz
                    """;
            case EN -> """
                    <b>👋 Welcome to PulQadam!</b>

                    PulQadam is more than an income and expense tracker. It's your companion for understanding your finances, making informed decisions and achieving your financial goals.

                    <b>🌳 With PulQadam, you can:</b>
                    - 💰 Keep track of your income and expenses
                    - 📊 Analyze your finances and compare periods
                    - 🧠 Make smarter, data-driven financial decisions
                    - 🎯 Set savings goals and work towards achieving them
                    - 📈 Monitor your financial progress
                    - 📁 Download reports in PDF and CSV

                    🔒 PulQadam stores only your Telegram ID as user information.

                    <b>🚀 Take your first step towards your financial goals today!</b>

                    🌐 pulqadam.uz
                    """;
        };
    }

    public void initializeMessage(BotContext context) {
        onboardingMessage.sendLanguageChoice(context);
    }
}