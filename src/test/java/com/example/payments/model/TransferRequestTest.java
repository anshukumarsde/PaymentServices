package com.example.payments.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TransferRequestTest {

    @Test
    void rejectsInvalidTransferDetails() {
        assertThrows(IllegalArgumentException.class,
                () -> new TransferRequest("1001", "1001", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class,
                () -> new TransferRequest("1001", "1002", BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> new TransferRequest("1001", "1002", new BigDecimal("1.001")));
    }
}
