package com.example.moneyflowapi.auth;

import com.example.moneyflowapi.auth.dto.request.AuthenticationRequest;
import com.example.moneyflowapi.auth.dto.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {

        var response = authService.authenticate(request);

        return ResponseEntity.ok(response);
    }

    // TODO: add register and refresh token logic
}
