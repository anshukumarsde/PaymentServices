package com.example.payments.service;

import com.example.payments.model.Payment;
import com.example.payments.model.PaymentRequest;
import com.example.payments.model.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentServiceTest {

    private final PaymentService paymentService = new PaymentService();

    @Test
    void createsPendingPaymentAndMakesItRetrievable() {
        Payment payment = paymentService.createPayment(validRequest());

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
        Payment payment = paymentService.createPayment(validRequest());

        Payment updated = paymentService.updatePaymentStatus(payment.getId(), PaymentStatus.SETTLED);

        assertEquals(PaymentStatus.SETTLED, updated.getStatus());
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.updatePaymentStatus(payment.getId(), PaymentStatus.REJECTED)
        );
    }

    @Test
    void rejectsPendingStatusAndReturnsNullForUnknownPayment() {
        Payment payment = paymentService.createPayment(validRequest());

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.updatePaymentStatus(payment.getId(), PaymentStatus.PENDING)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.updatePaymentStatus(payment.getId(), null)
        );
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
