package com.example.moneyflowapi.auth;

import com.example.moneyflowapi.auth.dto.request.AuthenticationRequest;
import com.example.moneyflowapi.auth.dto.response.AuthUserResponse;
import com.example.moneyflowapi.auth.dto.response.AuthenticationResponse;

public interface AuthService {
    AuthenticationResponse authenticate(AuthenticationRequest request);

    AuthUserResponse me();
}
