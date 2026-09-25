package com.example.payments.service;

import com.example.payments.model.Account;
import com.example.payments.model.CreateAccountRequest;
import com.example.payments.model.UpdateAccountRequest;
import com.example.payments.repository.AccountRepository;
import com.example.payments.repository.TransferRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    public AccountService(AccountRepository accountRepository, TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        if (accountRepository.existsById(request.accountNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account number already exists.");
        }
        return accountRepository.save(new Account(
                request.accountNumber(),
                request.accountHolderName(),
                request.openingBalance()
        ));
    }

    @Transactional(readOnly = true)
    public List<Account> listAccounts() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Account getAccount(String accountNumber) {
        return accountRepository.findById(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account number was not found."));
    }

    @Transactional
    public Account updateAccount(String accountNumber, UpdateAccountRequest request) {
        Account account = getAccount(accountNumber);
        account.updateAccountHolderName(request.accountHolderName());
        return account;
    }

    @Transactional
    public void deleteAccount(String accountNumber) {
        Account account = accountRepository.lockByAccountNumbers(List.of(accountNumber)).stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account number was not found."));

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only accounts with a zero balance can be deleted.");
        }
        if (transferRepository.existsByFromAccountNumberOrToAccountNumber(accountNumber, accountNumber)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Accounts with transfer history cannot be deleted.");
        }

        accountRepository.delete(account);
    }
}
