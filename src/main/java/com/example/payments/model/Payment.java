package com.example.payments.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Payment {

    private String id;
    private String sourceAccount;
    private String destinationAccount;
    private BigDecimal amount;
    private PaymentStatus status;

    public Payment() {
        this.id = UUID.randomUUID().toString();
        this.status = PaymentStatus.PENDING;
    }

    public Payment(String sourceAccount, String destinationAccount, BigDecimal amount) {
        this.id = UUID.randomUUID().toString();
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(String sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(String destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
