package com.example.moneyflowapi.telegram.bot;

import com.example.moneyflowapi.currency.Currency;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.telegram.exception.BotTransactionException;
import com.example.moneyflowapi.telegram.message.BotText;
import com.example.moneyflowapi.telegram.parser.ParsedTransaction;
import com.example.moneyflowapi.telegram.parser.TransactionMessageParser;
import com.example.moneyflowapi.telegram.resolver.BotLangResolver;
import com.example.moneyflowapi.telegram.service.BotTransactionService;
import com.example.moneyflowapi.telegram.service.SavedTransaction;
import com.example.moneyflowapi.transaction.TransactionType;
import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.user.UserLang;
import com.example.moneyflowapi.user.imp.UserServiceImp;
import com.example.moneyflowapi.userAccount.UserAccountService;
import com.example.moneyflowapi.userAccount.dto.request.UpdateLanguageRequest;
import com.example.moneyflowapi.userAccount.dto.request.UserStartRequest;
import io.github.solmosov.telegrambot.annotation.BotHandler;
import io.github.solmosov.telegrambot.annotation.CallbackQueryHandler;
import io.github.solmosov.telegrambot.annotation.CommandHandler;
import io.github.solmosov.telegrambot.annotation.MessageHandler;
import io.github.solmosov.telegrambot.context.BotContext;
import io.github.solmosov.telegrambot.keyboard.inline.InlineKeyboard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.ContentHandler;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@BotHandler("money_flow_bot")
@RequiredArgsConstructor
public class MoneyFlowBot {

    public static final String CB_DELETE = "tx_del:";
    public static final String CB_LANG = "lang:";

    private final BotStartMessage startMessage;
    private final TransactionMessageParser parser;
    private final BotLangResolver langResolver;
    private final UserServiceImp userServiceImp;
    private final UserAccountService userAccountService;
    private final BotOnboardingMessage onboardingMessage;
    private final BotTransactionService botTransactionService;

    @CommandHandler("/start")
    public void start(BotContext context) {
        Long chatId = context.message().chatId();
        User user = userServiceImp.registerUserByTelegram(chatId);

        startMessage.sendWelcomeMessage(context);

        if (user.getInitializedAt() != null) {
            return;
        }

        if (user.getLang() == null) {
            onboardingMessage.sendLanguageChoice(context);
        } else {
            onboardingMessage.sendCurrencyChoice(context, user.getLang());
        }
    }

    @CommandHandler("/help")
    public void help(BotContext context) {
        UserLang lang = langResolver.resolve(context);

        var keyboard = InlineKeyboard.of(
                InlineKeyboard.buttonWebApp(BotStartMessage.openAppButton(lang), BotStartMessage.WEB_APP_URL)
        );

        context.reply(helpText(lang))
                .keyboard(keyboard)
                .options(options -> options.html())
                .send();
    }

    private String helpText(UserLang lang) {
        return switch (lang) {
            case UZ -> """
                <b>💡 Qanday ishlaydi</b>

                Shunchaki yozing, men saqlab qo'yaman:

                <code>taksi 15000</code> → xarajat
                <code>oylik +5000000</code> → daromad

                Xato qildingizmi? Xabar ostidagi <b>🗑 O'chirish</b> tugmasini bosing.
                """;
            case RU -> """
                <b>💡 Как это работает</b>

                Просто напишите, я всё сохраню:

                <code>такси 15000</code> → расход
                <code>зарплата +5000000</code> → доход

                Ошиблись? Нажмите <b>🗑 Удалить</b> под сообщением.
                """;
            case EN -> """
                <b>💡 How it works</b>

                Just type it, and I'll save it:

                <code>taxi 15000</code> → expense
                <code>salary +5000000</code> → income

                Made a mistake? Tap <b>🗑 Delete</b> under the message.
                """;
        };
    }

    @MessageHandler(BotOnboardingMessage.LANG_UZ)
    public void onUzSelected(BotContext context) {
        selectLanguage(context, UserLang.UZ);
    }

    @MessageHandler(BotOnboardingMessage.LANG_RU)
    public void onRuSelected(BotContext context) {
        selectLanguage(context, UserLang.RU);
    }

    @MessageHandler(BotOnboardingMessage.LANG_EN)
    public void onEnSelected(BotContext context) {
        selectLanguage(context, UserLang.EN);
    }

    @CommandHandler("/language")
    public void language(BotContext context) {
        User user = userServiceImp.registerUserByTelegram(context.message().chatId());

        if (user.getInitializedAt() == null) {
            if (user.getLang() == null) {
                onboardingMessage.sendLanguageChoice(context);
            } else {
                onboardingMessage.sendCurrencyChoice(context, user.getLang());
            }
            return;
        }

        UserLang lang = user.getLang() != null ? user.getLang() : UserLang.EN;

        var keyboard = InlineKeyboard.of(
                InlineKeyboard.row(
                        InlineKeyboard.button(BotOnboardingMessage.LANG_UZ, CB_LANG + UserLang.UZ.name()),
                        InlineKeyboard.button(BotOnboardingMessage.LANG_RU, CB_LANG + UserLang.RU.name()),
                        InlineKeyboard.button(BotOnboardingMessage.LANG_EN, CB_LANG + UserLang.EN.name())
                )
        );

        context.reply(languageText(lang))
                .keyboard(keyboard)
                .options(options -> options.html())
                .send();
    }

    @CallbackQueryHandler(CB_LANG + "{code}")
    public void changeLanguage(BotContext context) {
        User user = userServiceImp.registerUserByTelegram(context.message().chatId());
        UserLang current = user.getLang() != null ? user.getLang() : UserLang.EN;

        UserLang selected;
        try {
            selected = UserLang.fromHeader(String.valueOf(context.callbackQuery().getCallbackParams().get("code")));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid language callback");
            context.reply(languageFailedText(current)).send();
            return;
        }

        try {
            userAccountService.changeLanguage(user.getId(), new UpdateLanguageRequest(selected));
            context.editMessage(context.messageId(), languageChangedText(selected)).send();
        } catch (RuntimeException e) {
            log.error("Language change failed", e);
            context.reply(languageFailedText(current)).send();
        }
    }

    private String languageText(UserLang lang) {
        return switch (lang) {
            case UZ -> "<b>🌐 Tilni tanlang</b>";
            case RU -> "<b>🌐 Выберите язык</b>";
            case EN -> "<b>🌐 Choose language</b>";
        };
    }

    private String languageChangedText(UserLang lang) {
        return switch (lang) {
            case UZ -> "✅ Til o'zgartirildi";
            case RU -> "✅ Язык изменён";
            case EN -> "✅ Language changed";
        };
    }

    private String languageFailedText(UserLang lang) {
        return switch (lang) {
            case UZ -> "Tilni o'zgartirib bo'lmadi. Keyinroq qayta urinib ko'ring.";
            case RU -> "Не удалось изменить язык. Попробуйте позже.";
            case EN -> "Couldn't change the language. Please try again later.";
        };
    }
    private void selectLanguage(BotContext context, UserLang lang) {
        User user = userServiceImp.registerUserByTelegram(context.message().chatId());

        if (user.getInitializedAt() != null) {
            return;
        }

        userAccountService.changeLanguage(user.getId(), new UpdateLanguageRequest(lang));
        onboardingMessage.sendCurrencyChoice(context, lang);
    }

    @CallbackQueryHandler(CB_DELETE + "{id}")
    public void deleteTransaction(BotContext context) {
        User user = userServiceImp.registerUserByTelegram(context.message().chatId());
        UserLang lang = user.getLang() != null ? user.getLang() : UserLang.EN;

        UUID id;
        try {
            id = UUID.fromString(String.valueOf(context.callbackQuery().getCallbackParams().get("id")));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid delete callback id");
            context.reply(deleteFailedText(lang)).send();
            return;
        }

        try {
            botTransactionService.delete(user, id);
            context.editMessage(context.messageId(), deletedText(lang))
                    .send();
        } catch (BusinessException e) {
            log.warn("Bot delete rejected: {}", e.getMessage());
            context.reply(deleteFailedText(lang)).send();
        } catch (RuntimeException e) {
            log.error("Bot delete failed", e);
            context.reply(deleteFailedText(lang)).send();
        }
    }

    @MessageHandler
    public void anyMessage(BotContext context) {
        String text = context.message().text();

        if (text == null || text.startsWith("/") || onboardingMessage.isLanguageLabel(text)) {
            return;
        }

        User user = userServiceImp.registerUserByTelegram(context.message().chatId());

        if (user.getInitializedAt() == null) {
            handleOnboarding(context, user, text);
            return;
        }

        UserLang lang = langResolver.resolve(context);
        Optional<ParsedTransaction> parsed = parser.parse(text);

        if (parsed.isEmpty()) {
            context.reply(BotText.PARSE_ERROR.get(lang)).send();
            return;
        }

        try {
            SavedTransaction saved = botTransactionService.save(user, parsed.get());
            sendSaved(context, lang, saved);
        } catch (BotTransactionException e) {
            context.reply(e.text().get(lang, e.args())).send();
        } catch (BusinessException e) {
            log.warn("Bot transaction rejected: {}", e.getMessage());
            context.reply(BotText.SAVE_FAILED.get(lang)).send();
        } catch (RuntimeException e) {
            log.error("Bot transaction failed", e);
            context.reply(BotText.SAVE_FAILED.get(lang)).send();
        }
    }

    private void sendSaved(BotContext context, UserLang lang, SavedTransaction saved) {
        BotText typeText = saved.type() == TransactionType.INCOME
                ? BotText.TYPE_INCOME
                : BotText.TYPE_EXPENSE;

        String html = BotText.TRANSACTION_SAVED.get(
                lang,
                typeText.get(lang),
                saved.category(),
                saved.amount().stripTrailingZeros().toPlainString(),
                saved.currency().name(),
                saved.walletName(),
                saved.note() == null ? "-" : saved.note()
        );

        var keyboard = InlineKeyboard.of(
                InlineKeyboard.row(
                        InlineKeyboard.buttonWebApp(BotStartMessage.openAppButton(lang), BotStartMessage.WEB_APP_URL),
                        InlineKeyboard.button(deleteButton(lang), CB_DELETE + saved.id())
                )
        );

        context.reply(html)
                .keyboard(keyboard)
                .options(options -> options.html())
                .send();
    }

    private String deleteButton(UserLang lang) {
        return switch (lang) {
            case UZ -> "🗑 Öçiriş";
            case RU -> "🗑 Удалить";
            case EN -> "🗑 Delete";
        };
    }

    private String deletedText(UserLang lang) {
        return switch (lang) {
            case UZ -> "🗑 Öçirildi";
            case RU -> "🗑 Удалено";
            case EN -> "🗑 Deleted";
        };
    }

    private String deleteFailedText(UserLang lang) {
        return switch (lang) {
            case UZ -> "Bu yozuvni öçirib bölmadi. Balki u allaqaçon öçirilgan.";
            case RU -> "Не удалось удалить запись. Возможно, она уже удалена.";
            case EN -> "Couldn't delete this entry. It may already be deleted.";
        };
    }

    private void handleOnboarding(BotContext context, User user, String text) {
        if (user.getLang() == null) {
            onboardingMessage.sendLanguageChoice(context);
            return;
        }

        Optional<Currency> selected = onboardingMessage.parseCurrency(text);

        if (selected.isEmpty()) {
            onboardingMessage.sendCurrencyChoice(context, user.getLang());
            return;
        }

        userAccountService.initialize(
                user.getId(),
                user.getLang().name(),
                new UserStartRequest(selected.get().getCode())
        );
        onboardingMessage.sendInitialized(context, user.getLang());
    }
}