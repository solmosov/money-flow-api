package com.example.moneyflowapi.userAccount;

import com.example.moneyflowapi.userAccount.dto.request.UserDeleteAccountRequest;
import com.example.moneyflowapi.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountDeletionService accountDeletionService;

    @PostMapping("/deletion-code")
    public ResponseEntity<Void> requestCode(){
        accountDeletionService.requestCode(SecurityUtil.getCurrentUserId());
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@Valid @RequestBody UserDeleteAccountRequest request){
        accountDeletionService.confirm(SecurityUtil.getCurrentUserId(), request.code());
        return ResponseEntity.noContent().build();
    }
}
