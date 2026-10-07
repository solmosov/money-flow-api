package com.example.moneyflowapi.auth.imp;

import com.example.moneyflowapi.auth.TelegramAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramAuthServiceImp implements TelegramAuthService {

    @Value("${app.telegram.bot-token}")
    private String botToken;

    @Override
    public String verify(String initData) throws Exception {
        Map<String, String> params = new HashMap<>();

        for (String part : initData.split("&")) {
            String[] pair = part.split("=", 2);

            if (pair.length == 2) {
                params.put(
                        URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
                );
            }
        }

        String receivedHash = params.remove("hash");

        if (receivedHash == null) {
            throw new RuntimeException("Missing hash");
        }

        String dataCheckString = params.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");

        byte[] secretKey = hmac(
                "WebAppData".getBytes(StandardCharsets.UTF_8),
                botToken.getBytes(StandardCharsets.UTF_8)
        );

        String calculatedHash = bytesToHex(
                hmac(
                        secretKey,
                        dataCheckString.getBytes(StandardCharsets.UTF_8)
                )
        );

        if (!MessageDigest.isEqual(
                calculatedHash.getBytes(StandardCharsets.UTF_8),
                receivedHash.getBytes(StandardCharsets.UTF_8)
        )) {
            throw new RuntimeException("Invalid initData");
        }

        String userJson = params.get("user");

        return userJson;
    }

    private byte[] hmac(byte[] key, byte[] data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }
}
