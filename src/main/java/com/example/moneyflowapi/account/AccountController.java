package com.example.moneyflowapi.account;

import com.example.moneyflowapi.account.dto.request.DeleteAccountRequest;
import com.example.moneyflowapi.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountDeletionService accountDeletionService;

    @PostMapping("/deletion-code")
    public ResponseEntity<Void> requestCode(){
        accountDeletionService.requestCode(SecurityUtil.getCurrentUserId());
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@Valid @RequestBody DeleteAccountRequest request){
        accountDeletionService.confirm(SecurityUtil.getCurrentUserId(), request.code());
        return ResponseEntity.noContent().build();
    }
}
