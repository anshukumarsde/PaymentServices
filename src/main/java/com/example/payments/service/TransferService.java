package com.example.payments.service;

import com.example.payments.model.Account;
import com.example.payments.model.Transfer;
import com.example.payments.model.TransferRequest;
import com.example.payments.repository.AccountRepository;
import com.example.payments.repository.TransferRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    public TransferService(AccountRepository accountRepository, TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public Transfer initiateTransfer(TransferRequest request) {
        List<Account> accounts = accountRepository.lockByAccountNumbers(
                List.of(request.fromAccountNumber(), request.toAccountNumber()).stream().sorted().toList()
        );
        if (accounts.size() != 2) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or both account numbers were not found.");
        }

        Account fromAccount = accounts.stream()
                .filter(account -> account.getAccountNumber().equals(request.fromAccountNumber()))
                .findFirst()
                .orElseThrow();
        Account toAccount = accounts.stream()
                .filter(account -> account.getAccountNumber().equals(request.toAccountNumber()))
                .findFirst()
                .orElseThrow();

        if (fromAccount.getBalance().compareTo(request.amount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds in the from account.");
        }

        fromAccount.debit(request.amount());
        toAccount.credit(request.amount());
        return transferRepository.save(new Transfer(
                fromAccount,
                toAccount,
                request.amount()
        ));
    }

}
