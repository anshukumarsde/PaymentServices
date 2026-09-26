package com.example.payments.controller;

import com.example.payments.model.Account;
import com.example.payments.model.CreateAccountRequest;
import com.example.payments.model.UpdateAccountRequest;
import com.example.payments.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping
    public List<Account> listAccounts() {
        return accountService.listAccounts();
    }

    @GetMapping("/{accountNumber}")
    public Account getAccount(@PathVariable String accountNumber) {
        return accountService.getAccount(accountNumber);
    }

    @PutMapping("/{accountNumber}")
    public Account updateAccount(
            @PathVariable String accountNumber,
            @RequestBody UpdateAccountRequest request
    ) {
        return accountService.updateAccount(accountNumber, request);
    }

    @DeleteMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable String accountNumber) {
        accountService.deleteAccount(accountNumber);
    }
}
