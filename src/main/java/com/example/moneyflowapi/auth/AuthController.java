package com.example.moneyflowapi.auth;

import com.example.moneyflowapi.auth.dto.request.AuthenticationByTelegramRequest;
import com.example.moneyflowapi.auth.dto.request.AuthenticationRequest;
import com.example.moneyflowapi.auth.dto.response.AuthenticationResponse;
import jakarta.validation.Valid;
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
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid  @RequestBody AuthenticationRequest request) {

        var response = authService.authenticate(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/telegram")
    public ResponseEntity<AuthenticationResponse> auth(@Valid @RequestBody AuthenticationByTelegramRequest body) throws Exception {
        return ResponseEntity.ok(telegramAuthService.verify(body.initData()));
    }

    // TODO: add register and refresh token logic

    // TODO: add rate limit validation for auth endpoints
}
