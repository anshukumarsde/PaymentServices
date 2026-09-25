package com.example.payments.repository;

import com.example.payments.model.Payment;
import com.example.payments.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void savesAndLoadsPaymentFromDatabase() {
        Payment payment = new Payment("source-1", "destination-1", new BigDecimal("12.50"));
        payment.setStatus(PaymentStatus.SETTLED);

        Payment savedPayment = paymentRepository.saveAndFlush(payment);
        entityManager.clear();

        Payment loadedPayment = paymentRepository.findById(savedPayment.getId()).orElseThrow();

        assertEquals(savedPayment.getId(), loadedPayment.getId());
        assertEquals("source-1", loadedPayment.getSourceAccount());
        assertEquals("destination-1", loadedPayment.getDestinationAccount());
        assertEquals(new BigDecimal("12.50"), loadedPayment.getAmount());
        assertEquals(PaymentStatus.SETTLED, loadedPayment.getStatus());
        assertTrue(paymentRepository.findByIdForUpdate(savedPayment.getId()).isPresent());
    }
}
