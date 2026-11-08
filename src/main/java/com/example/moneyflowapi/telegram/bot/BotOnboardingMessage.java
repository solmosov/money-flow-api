package com.example.moneyflowapi.telegram.bot;

import com.example.moneyflowapi.currency.Currency;
import com.example.moneyflowapi.currency.CurrencyCode;
import com.example.moneyflowapi.currency.CurrencyRepository;
import com.example.moneyflowapi.user.UserLang;
import io.github.solmosov.telegrambot.context.BotContext;
import io.github.solmosov.telegrambot.keyboard.inline.InlineKeyboard;
import io.github.solmosov.telegrambot.keyboard.reply.ReplyKeyboard;
import io.github.solmosov.telegrambot.keyboard.reply.ReplyKeyboardButton;
import io.github.solmosov.telegrambot.keyboard.reply.ReplyKeyboardElement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BotOnboardingMessage {

    public static final String LANG_UZ = "🇺🇿 Özbekça";
    public static final String LANG_RU = "🇷🇺 Русский";
    public static final String LANG_EN = "🇬🇧 English";

    private static final List<CurrencyCode> POPULAR_CURRENCIES = List.of(
            CurrencyCode.UZS,
            CurrencyCode.USD,
            CurrencyCode.EUR,
            CurrencyCode.RUB,
            CurrencyCode.KZT
    );

    private static final String WEB_APP_URL = "https://dash.pulqadam.uz";
    private static final int CURRENCIES_PER_ROW = 3;

    private final CurrencyRepository currencyRepository;

    public void sendLanguageChoice(BotContext context) {
        var keyboard = ReplyKeyboard.of(
                ReplyKeyboard.row(
                        ReplyKeyboard.button(LANG_UZ),
                        ReplyKeyboard.button(LANG_RU),
                        ReplyKeyboard.button(LANG_EN)
                )
        );

        context.reply("""
                        <b>🌐 Tilni tanlang / Выберите язык / Choose language</b>
                        """)
                .keyboard(keyboard)
                .options(options -> options.html())
                .send();
    }

    public Optional<UserLang> parseLanguage(String text) {
        if (text == null) {
            return Optional.empty();
        }

        return switch (text.trim()) {
            case LANG_UZ -> Optional.of(UserLang.UZ);
            case LANG_RU -> Optional.of(UserLang.RU);
            case LANG_EN -> Optional.of(UserLang.EN);
            default -> Optional.empty();
        };
    }

    public boolean isLanguageLabel(String text) {
        return parseLanguage(text).isPresent();
    }

    public void sendCurrencyChoice(BotContext context, UserLang lang) {
        List<Currency> currencies = popularCurrencies();

        List<Object> rows = new ArrayList<>();

        for (int i = 0; i < currencies.size(); i += CURRENCIES_PER_ROW) {
            var chunk = currencies.subList(i, Math.min(i + CURRENCIES_PER_ROW, currencies.size()));
            rows.add(ReplyKeyboard.row(
                    chunk.stream()
                            .map(currency -> ReplyKeyboard.button(currencyLabel(currency)))
                            .toArray(ReplyKeyboardButton[]::new)
            ));
        }

        var keyboard = ReplyKeyboard.of(rows.toArray(new ReplyKeyboardElement[0]));

        context.reply(currencyText(lang))
                .keyboard(keyboard)
                .options(options -> options.html())
                .send();
    }

    public Optional<Currency> parseCurrency(String text) {
        if (text == null) {
            return Optional.empty();
        }

        String value = text.trim();

        return popularCurrencies().stream()
                .filter(currency -> value.equals(currencyLabel(currency)))
                .findFirst();
    }

    public void sendInitialized(BotContext context, UserLang lang) {
        context.reply(initializedText(lang))
                .keyboard(ReplyKeyboard.removeKeyboard())
                .options(options -> options.html())
                .send();

        var appKeyboard = InlineKeyboard.of(
                InlineKeyboard.buttonWebApp(BotStartMessage.openAppButton(lang), WEB_APP_URL)
        );

        context.reply(openAppText(lang))
                .keyboard(appKeyboard)
                .send();
    }

    private List<Currency> popularCurrencies() {
        Map<CurrencyCode, Currency> byCode = currencyRepository.findAllByIsActiveTrueAndIsPopularTrueOrderByCodeAsc().stream()
                .collect(Collectors.toMap(Currency::getCode, Function.identity()));

        return POPULAR_CURRENCIES.stream()
                .map(byCode::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private String currencyLabel(Currency currency) {
        return currency.getSymbol() + " " + currency.getCode().name();
    }

    private String currencyText(UserLang lang) {
        return switch (lang) {
            case UZ -> """
                <b>🟢 Asosiy valyutani tanlang</b>

                Hisobingiz şu valyutada yuritiladi. Boşqa valyutalarni keyinroq ilovada tanlaşingiz mumkin.
                """;
            case RU -> """
                <b>🟢 Выберите основную валюту</b>

                Ваш учёт будет вестись в этой валюте. Другие валюты можно выбрать позже в приложении.
                """;
            case EN -> """
                <b>🟢 Choose your main currency</b>

                Your account will be tracked in this currency. You can pick other currencies later in the app.
                """;
        };
    }

    private String openAppText(UserLang lang) {
        return switch (lang) {
            case UZ -> "Ilovani ham oçişingiz mumkin:";
            case RU -> "Также можно открыть приложение:";
            case EN -> "You can also open the app:";
        };
    }

    private String initializedText(UserLang lang) {
        return switch (lang) {
            case UZ -> """
                <b>🎉 Tayyor!</b>

                Endi daromad va xarajatlarni şu yerning öziga yozişingiz mumkin. Masalan: <code>taksi 15000</code>
                """;
            case RU -> """
                <b>🎉 Готово!</b>

                Теперь можно записывать доходы и расходы прямо здесь. Например: <code>такси 15000</code>
                """;
            case EN -> """
                <b>🎉 All set!</b>

                You can now log income and expenses right here. For example: <code>taxi 15000</code>
                """;
        };
    }
}