package com.example.payments.model;

public record UpdateAccountRequest(String accountHolderName) {

    public UpdateAccountRequest {
        if (accountHolderName == null || accountHolderName.isBlank()) {
            throw new IllegalArgumentException("Account holder name is required.");
        }
    }
}
