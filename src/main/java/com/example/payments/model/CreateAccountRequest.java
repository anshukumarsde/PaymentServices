package com.example.payments.model;

import java.math.BigDecimal;

public record CreateAccountRequest(String accountNumber, String accountHolderName, BigDecimal openingBalance) {

    public CreateAccountRequest {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number is required.");
        }
        if (accountHolderName == null || accountHolderName.isBlank()) {
            throw new IllegalArgumentException("Account holder name is required.");
        }
        if (openingBalance == null || openingBalance.signum() < 0 || openingBalance.scale() > 2) {
            throw new IllegalArgumentException("Opening balance must be zero or greater and have at most two decimal places.");
        }
    }
}
