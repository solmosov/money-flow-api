package com.example.moneyflowapi.auth.imp;

import com.example.moneyflowapi.auth.AuthService;
import com.example.moneyflowapi.auth.dto.request.AuthenticationRequest;
import com.example.moneyflowapi.auth.dto.response.AuthenticationResponse;
import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import com.example.moneyflowapi.security.jwt.JwtService;
import com.example.moneyflowapi.security.jwt.dto.GenerateAccessTokenDto;
import com.example.moneyflowapi.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        final Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var user = (User) auth.getPrincipal();

        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String tokenType = "Bearer";
        String accessToken = jwtService.generateAccessToken(
                GenerateAccessTokenDto.builder()
                        .userId(user.getId())
                        .userEmail(user.getEmail())
                        .build()
        );

        return AuthenticationResponse.builder()
                .tokenType(tokenType)
                .accessToken(accessToken)
                .build();
    }
}
