package com.example.moneyflowapi.auth.imp;

import com.example.moneyflowapi.auth.TelegramAuthService;
import com.example.moneyflowapi.auth.dto.response.AuthenticationResponse;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.security.jwt.JwtService;
import com.example.moneyflowapi.security.jwt.dto.GenerateAccessTokenDto;
import com.example.moneyflowapi.user.User;
import com.example.moneyflowapi.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TelegramAuthServiceImp implements TelegramAuthService {

    @Value("${app.telegram.bot-token}")
    private String botToken;

    private final UserService userService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    public AuthenticationResponse verify(String initData) throws Exception {
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
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
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
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String userJson = params.get("user");

        JsonNode user = objectMapper.readTree(userJson);
        long telegramUserId = user.get("id").asLong();

        User registeredUser = userService.registerUserByTelegram(telegramUserId);

        String accessToken = jwtService.generateAccessToken(
                GenerateAccessTokenDto.builder()
                        .userId(registeredUser.getId())
                        .userEmail(registeredUser.getEmail())
                        .build()
        );

        return AuthenticationResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .build();
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
