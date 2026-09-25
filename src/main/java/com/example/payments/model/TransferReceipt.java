package com.example.payments.model;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferReceipt(
        String transferId,
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount,
        TransferStatus status,
        Instant createdAt
) {

    public static TransferReceipt from(Transfer transfer) {
        return new TransferReceipt(
                transfer.getTransferId(),
                transfer.getFromAccount().getAccountNumber(),
                transfer.getToAccount().getAccountNumber(),
                transfer.getAmount(),
                transfer.getStatus(),
                transfer.getCreatedAt()
        );
    }
}
