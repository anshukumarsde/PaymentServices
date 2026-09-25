package com.example.payments.service;

import com.example.payments.model.Payment;
import com.example.payments.model.PaymentRequest;
import com.example.payments.repository.PaymentRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    private final PaymentRepository repository = mock(PaymentRepository.class);
    private final PaymentService service = new PaymentService(repository);

    @Test
    void createsPayment() {
        when(repository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        PaymentRequest request = new PaymentRequest("source", "destination", new BigDecimal("12.50"));

        Payment payment = service.create(request);

        assertEquals("source", payment.getSourceAccount());
        assertEquals("destination", payment.getDestinationAccount());
        assertEquals(new BigDecimal("12.50"), payment.getAmount());
        verify(repository).save(any(Payment.class));
    }

    @Test
    void returnsPaymentsFromRepository() {
        List<Payment> payments = List.of(new Payment("source", "destination", BigDecimal.ONE));
        when(repository.findAll()).thenReturn(payments);

        assertEquals(payments, service.findAll());
    }

    @Test
    void findsPaymentById() {
        Payment payment = new Payment("source", "destination", BigDecimal.ONE);
        when(repository.findById(payment.getId())).thenReturn(Optional.of(payment));

        assertTrue(service.findById(payment.getId()).isPresent());
        assertEquals(payment, service.findById(payment.getId()).orElseThrow());
    }
}
