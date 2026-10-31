package com.example.moneyflowapi.telegram.service;

import com.example.moneyflowapi.telegram.handler.TelegramBotHandler.StartReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class TelegramSender {

    private static final Logger log = LoggerFactory.getLogger(TelegramSender.class);

    private final RestClient restClient;

    public TelegramSender(@Value("${app.telegram.bot-token}") String botToken) {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.telegram.org/bot" + botToken)
                .build();
    }

    public void sendStart(StartReply reply) {
        Map<String, Object> body = Map.of(
                "chat_id", reply.chatId(),
                "photo", reply.photoUrl(),
                "caption", reply.html(),
                "parse_mode", "HTML",
                "reply_markup", Map.of(
                        "inline_keyboard", List.of(
                                List.of(Map.of(
                                        "text", reply.buttonText(),
                                        "web_app", Map.of("url", reply.webAppUrl())
                                ))
                        )
                )
        );

        try {
            restClient.post()
                    .uri("/sendPhoto")
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Failed to send /start reply to chat {}", reply.chatId(), e);
        }
    }
}