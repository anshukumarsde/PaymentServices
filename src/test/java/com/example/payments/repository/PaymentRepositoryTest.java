package com.example.payments.repository;

import com.example.payments.model.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository repository;

    @Test
    void savesAndFindsPayment() {
        Payment payment = repository.save(
                new Payment("source", "destination", new BigDecimal("12.50"))
        );

        Payment found = repository.findById(payment.getId()).orElseThrow();

        assertEquals(payment.getId(), found.getId());
        assertEquals("source", found.getSourceAccount());
        assertEquals("destination", found.getDestinationAccount());
        assertEquals(new BigDecimal("12.50"), found.getAmount());
    }
}
