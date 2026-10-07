package com.example.moneyflowapi.auth;

import com.example.moneyflowapi.auth.dto.request.AuthenticationRequest;
import com.example.moneyflowapi.auth.dto.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TelegramAuthService telegramAuthService;

    @PostMapping("login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {

        var response = authService.authenticate(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/telegram")
    public ResponseEntity<String> auth(@RequestBody Map<String, String> body) throws Exception {
        String initData = body.get("initData");

        return ResponseEntity.ok(telegramAuthService.verify(initData));
    }

    // TODO: add register and refresh token logic
}
