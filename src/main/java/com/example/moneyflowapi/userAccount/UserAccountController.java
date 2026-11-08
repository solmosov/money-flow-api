package com.example.moneyflowapi.userAccount;

import com.example.moneyflowapi.userAccount.dto.request.UpdateBaseCurrencyRequest;
import com.example.moneyflowapi.userAccount.dto.request.UpdateLanguageRequest;
import com.example.moneyflowapi.userAccount.dto.request.UserDeleteAccountRequest;
import com.example.moneyflowapi.security.SecurityUtil;
import com.example.moneyflowapi.userAccount.dto.request.UserStartRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jdk.jshell.Snippet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="User account", description = "User account management")
@RestController
@RequestMapping("/api/v1/user-account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountDeletionService accountDeletionService;
    private final UserAccountService userAccountService;

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void start(
            @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, required = false) String language,
            @Valid @RequestBody UserStartRequest request
    ){
        userAccountService.initialize(SecurityUtil.getCurrentRealUserId(), language, request);
    }

    @PutMapping("/change-language")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeLanguage(
            @Valid @RequestBody UpdateLanguageRequest request
    ){
        userAccountService.changeLanguage(SecurityUtil.getCurrentRealUserId(), request);
    }

    @PutMapping("/change-base-currency")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeBaseCurrency(
            @Valid @RequestBody UpdateBaseCurrencyRequest request
    ){
        userAccountService.changeBaseCurrency(SecurityUtil.getCurrentRealUserId(), request);
    }

    @PostMapping("/deletion-code")
    public ResponseEntity<Void> requestCode(){
        accountDeletionService.requestCode(SecurityUtil.getCurrentRealUserId());
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@Valid @RequestBody UserDeleteAccountRequest request){
        accountDeletionService.confirm(SecurityUtil.getCurrentRealUserId(), request.code());
    }

    @DeleteMapping("/demo-data")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDemoData(){
        userAccountService.deleteDemoData(SecurityUtil.getCurrentRealUserId());
    }
}
