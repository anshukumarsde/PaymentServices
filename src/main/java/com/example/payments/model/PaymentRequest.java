package com.example.payments.model;

import java.math.BigDecimal;

public record PaymentRequest(String sourceAccount, String destinationAccount, BigDecimal amount) {
    public PaymentRequest {
        if (sourceAccount == null || sourceAccount.isBlank()) {
            throw new IllegalArgumentException("Source account is required.");
        }
        if (destinationAccount == null || destinationAccount.isBlank()) {
            throw new IllegalArgumentException("Destination account is required.");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }
}
