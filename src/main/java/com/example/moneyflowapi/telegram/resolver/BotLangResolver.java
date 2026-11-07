package com.example.moneyflowapi.telegram.resolver;

import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.user.UserLang;
import com.example.moneyflowapi.user.UserRepository;
import io.github.solmosov.telegrambot.context.BotContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotLangResolver {

    private final UserRepository userRepository;

    public UserLang resolve(BotContext context) {
        var from = context.message().from();

        return userRepository.findByTelegramUserId(from.id())
                .map(User::getLang)
                .orElseGet(() -> UserLang.fromHeader(from.languageCode()));
    }
}
