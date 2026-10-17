package com.example.moneyflowapi.user;

import com.example.moneyflowapi.user.dto.response.AuthUserResponse;
import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.user.dto.response.BalanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<AuthUserResponse> me() {
        return ResponseEntity.ok(userService.getMe());
    }

    @GetMapping("/me/balance")
    public ResponseEntity<BalanceResponse> getBalance() {
        return ResponseEntity.ok(userService.getBalance(SecurityUtil.getCurrentUserId()));
    }

    @DeleteMapping("/me/delete-account")
    public ResponseEntity<Void> deleteAccount() {

        userService.deleteAccount(SecurityUtil.getCurrentUserId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
