package com.example.payments.repository;

import com.example.payments.model.Transfer;
import com.example.payments.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class TransferRepositoryTest {

    @Autowired
    private TransferRepository repository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void savesAndFindsTransfer() {
        Account fromAccount = accountRepository.save(new Account("1001", "Alex Morgan", new BigDecimal("100.00")));
        Account toAccount = accountRepository.save(new Account("1002", "Jordan Lee", new BigDecimal("50.00")));
        Transfer transfer = repository.save(new Transfer(fromAccount, toAccount, new BigDecimal("25.00")));

        Transfer saved = repository.findById(transfer.getTransferId()).orElseThrow();

        assertEquals("1001", saved.getFromAccount().getAccountNumber());
        assertEquals("1002", saved.getToAccount().getAccountNumber());
        assertEquals(new BigDecimal("25.00"), saved.getAmount());
        assertNotNull(fromAccount.getCreatedAt());
    }
}
