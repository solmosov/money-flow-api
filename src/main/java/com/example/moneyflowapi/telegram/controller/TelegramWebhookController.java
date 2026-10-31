package com.example.moneyflowapi.telegram.controller;

import com.example.moneyflowapi.telegram.dto.Update;
import com.example.moneyflowapi.telegram.handler.TelegramBotHandler;
import com.example.moneyflowapi.telegram.service.TelegramSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/telegram")
public class TelegramWebhookController {

    private final TelegramBotHandler handler;
    private final TelegramSender sender;
    private final String secretToken;

    public TelegramWebhookController(
            TelegramBotHandler handler,
            TelegramSender sender,
            @Value("${app.telegram.webhook-secret}") String secretToken
    ) {
        this.handler = handler;
        this.sender = sender;
        this.secretToken = secretToken;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> onUpdate(
            @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) String token,
            @RequestBody Update update) {

        if (!secretToken.equals(token)) {
            return ResponseEntity.status(403).build();
        }

        handler.handle(update).ifPresent(sender::sendStart);

        return ResponseEntity.ok().build();
    }
}