package com.example.payments.service;

import com.example.payments.model.Payment;
import com.example.payments.model.PaymentRequest;
import com.example.payments.model.PaymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentService {

    private final Map<String, Payment> payments = new ConcurrentHashMap<>();

    public List<Payment> getPayments() {
        return new ArrayList<>(payments.values());
    }

    public Payment getPayment(String id) {
        return payments.get(id);
    }

    public Payment updatePaymentStatus(String id, PaymentStatus status) {
        if (status == null || status == PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Status must be SETTLED or REJECTED.");
        }

        Payment payment = payments.get(id);
        if (payment == null) {
            return null;
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Only pending payments can be updated.");
        }

        payment.setStatus(status);
        return payment;
    }

    public Payment createPayment(PaymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment details are required.");
        }

        if (request.getSourceAccount() == null || request.getSourceAccount().isBlank()) {
            throw new IllegalArgumentException("Source account is required.");
        }

        if (request.getDestinationAccount() == null || request.getDestinationAccount().isBlank()) {
            throw new IllegalArgumentException("Destination account is required.");
        }

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        Payment payment = new Payment(
                request.getSourceAccount(),
                request.getDestinationAccount(),
                amount
        );
        payment.setStatus(PaymentStatus.PENDING);
        payments.put(payment.getId(), payment);
        return payment;
    }
}
