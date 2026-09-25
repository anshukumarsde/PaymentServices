package com.example.payments.service;

import com.example.payments.model.Payment;
import com.example.payments.model.PaymentRequest;
import com.example.payments.model.PaymentStatus;
import com.example.payments.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository);
    }

    @Test
    void createsPendingPaymentAndMakesItRetrievable() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Payment payment = paymentService.createPayment(validRequest());
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        assertNotNull(payment.getId());
        assertEquals("source-1", payment.getSourceAccount());
        assertEquals("destination-1", payment.getDestinationAccount());
        assertEquals(new BigDecimal("12.50"), payment.getAmount());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertEquals(payment, paymentService.getPayment(payment.getId()));
        assertEquals(1, paymentService.getPayments().size());
    }

    @Test
    void rejectsMissingOrInvalidPaymentDetails() {
        assertThrows(IllegalArgumentException.class, () -> paymentService.createPayment(null));

        PaymentRequest missingSource = validRequest();
        missingSource.setSourceAccount(" ");
        assertThrows(IllegalArgumentException.class, () -> paymentService.createPayment(missingSource));

        PaymentRequest missingDestination = validRequest();
        missingDestination.setDestinationAccount("");
        assertThrows(IllegalArgumentException.class, () -> paymentService.createPayment(missingDestination));

        PaymentRequest zeroAmount = validRequest();
        zeroAmount.setAmount(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> paymentService.createPayment(zeroAmount));

        PaymentRequest negativeAmount = validRequest();
        negativeAmount.setAmount(new BigDecimal("-1"));
        assertThrows(IllegalArgumentException.class, () -> paymentService.createPayment(negativeAmount));
    }

    @Test
    void updatesPendingPaymentToTerminalStatusOnce() {
        Payment payment = new Payment("source-1", "destination-1", new BigDecimal("12.50"));
        when(paymentRepository.findByIdForUpdate(payment.getId())).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment updated = paymentService.updatePaymentStatus(payment.getId(), PaymentStatus.SETTLED);

        assertEquals(PaymentStatus.SETTLED, updated.getStatus());
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.updatePaymentStatus(payment.getId(), PaymentStatus.REJECTED)
        );
    }

    @Test
    void rejectsPendingStatusAndReturnsNullForUnknownPayment() {
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.updatePaymentStatus("payment-id", PaymentStatus.PENDING)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.updatePaymentStatus("payment-id", null)
        );
        when(paymentRepository.findByIdForUpdate("unknown-id")).thenReturn(Optional.empty());
        assertNull(paymentService.updatePaymentStatus("unknown-id", PaymentStatus.SETTLED));
    }

    private PaymentRequest validRequest() {
        PaymentRequest request = new PaymentRequest();
        request.setSourceAccount("source-1");
        request.setDestinationAccount("destination-1");
        request.setAmount(new BigDecimal("12.50"));
        return request;
    }
}
