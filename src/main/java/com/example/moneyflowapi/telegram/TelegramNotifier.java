package com.example.moneyflowapi.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@Component
public class TelegramNotifier {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private final String sendMessageUrl;

    public TelegramNotifier(@Value("${app.telegram.bot-token}") String botToken) {
        this.sendMessageUrl = "https://api.telegram.org/bot" + botToken + "/sendMessage";
    }

    public void send(Long telegramUserId, String text) {
        String body = "chat_id=" + telegramUserId
                + "&parse_mode=HTML"
                + "&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder(URI.create(sendMessageUrl))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .whenComplete((response, error) -> {
                    if(error != null ){
                        log.error("Telegram send failed: {}", error.getMessage());
                    }else if(response.statusCode() != 200){
                        log.warn("Telegram send returned status {}", response.statusCode());
                    }
                });
    }
}
