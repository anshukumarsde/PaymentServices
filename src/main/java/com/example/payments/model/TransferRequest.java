package com.example.payments.model;

import java.math.BigDecimal;

public record TransferRequest(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {

    public TransferRequest {
        if (fromAccountNumber == null || fromAccountNumber.isBlank()) {
            throw new IllegalArgumentException("From-account number is required.");
        }
        if (toAccountNumber == null || toAccountNumber.isBlank()) {
            throw new IllegalArgumentException("To-account number is required.");
        }
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("From and to accounts must be different.");
        }
        if (amount == null || amount.signum() <= 0 || amount.scale() > 2) {
            throw new IllegalArgumentException("Transfer amount must be positive and have at most two decimal places.");
        }
    }
}
