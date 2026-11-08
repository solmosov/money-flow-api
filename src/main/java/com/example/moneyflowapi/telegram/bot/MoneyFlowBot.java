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
import io.github.solmosov.telegrambot.annotation.CommandHandler;
import io.github.solmosov.telegrambot.annotation.MessageHandler;
import io.github.solmosov.telegrambot.context.BotContext;
import io.github.solmosov.telegrambot.keyboard.inline.InlineKeyboard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@BotHandler("money_flow_bot")
@RequiredArgsConstructor
public class MoneyFlowBot {

    public static final String CB_DELETE = "tx:del:";

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

    private void selectLanguage(BotContext context, UserLang lang) {
        User user = userServiceImp.registerUserByTelegram(context.message().chatId());

        if (user.getInitializedAt() != null) {
            return;
        }

        userAccountService.changeLanguage(user.getId(), new UpdateLanguageRequest(lang));
        onboardingMessage.sendCurrencyChoice(context, lang);
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
                        InlineKeyboard.button("Delete", CB_DELETE + saved.id())
                )
        );

        context.reply(html).keyboard(keyboard).send();
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