package com.example.payments.service;

import com.example.payments.model.Account;
import com.example.payments.model.TransferRequest;
import com.example.payments.repository.AccountRepository;
import com.example.payments.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class TransferServiceTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferRepository transferRepository;

    @BeforeEach
    void setUpAccounts() {
        transferRepository.deleteAll();
        accountRepository.deleteAll();
        accountRepository.save(new Account("1001", "Alex Morgan", new BigDecimal("100.00")));
        accountRepository.save(new Account("1002", "Jordan Lee", new BigDecimal("50.00")));
    }

    @Test
    void transfersFundsAndRecordsCompletedTransfer() {
        var transfer = transferService.initiateTransfer(
                new TransferRequest("1001", "1002", new BigDecimal("25.00"))
        );

        assertEquals("COMPLETED", transfer.getStatus().name());
        assertEquals(new BigDecimal("75.00"), accountRepository.findById("1001").orElseThrow().getBalance());
        assertEquals(new BigDecimal("75.00"), accountRepository.findById("1002").orElseThrow().getBalance());
        assertEquals(1, transferRepository.count());
    }

    @Test
    void rejectsInsufficientFundsWithoutChangingBalances() {
        assertThrows(IllegalArgumentException.class, () -> transferService.initiateTransfer(
                new TransferRequest("1001", "1002", new BigDecimal("101.00"))
        ));

        assertEquals(new BigDecimal("100.00"), accountRepository.findById("1001").orElseThrow().getBalance());
        assertEquals(new BigDecimal("50.00"), accountRepository.findById("1002").orElseThrow().getBalance());
        assertEquals(0, transferRepository.count());
    }
}
