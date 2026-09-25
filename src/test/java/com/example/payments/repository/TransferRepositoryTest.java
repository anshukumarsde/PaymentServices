package com.example.payments.repository;

import com.example.payments.model.Transfer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class TransferRepositoryTest {

    @Autowired
    private TransferRepository repository;

    @Test
    void savesAndFindsTransfer() {
        Transfer transfer = repository.save(
                new Transfer("1001", "1002", new BigDecimal("25.00"))
        );

        Transfer saved = repository.findById(transfer.getTransferId()).orElseThrow();

        assertEquals("1001", saved.getFromAccountNumber());
        assertEquals("1002", saved.getToAccountNumber());
        assertEquals(new BigDecimal("25.00"), saved.getAmount());
    }
}
