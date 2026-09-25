package com.example.payments.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentRequestTest {

    @Test
    void rejectsMissingAccountsAndNonPositiveAmounts() {
        assertThrows(IllegalArgumentException.class,
                () -> new PaymentRequest(" ", "destination", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class,
                () -> new PaymentRequest("source", "", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class,
                () -> new PaymentRequest("source", "destination", BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> new PaymentRequest("source", "destination", null));
    }
}
