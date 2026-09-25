package com.example.payments.service;

import com.example.payments.model.Payment;
import com.example.payments.model.PaymentRequest;
import com.example.payments.model.PaymentStatus;
import com.example.payments.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public List<Payment> getPayments() {
        return paymentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Payment getPayment(String id) {
        return paymentRepository.findById(id).orElse(null);
    }

    @Transactional
    public Payment updatePaymentStatus(String id, PaymentStatus status) {
        if (status == null || status == PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Status must be SETTLED or REJECTED.");
        }

        Payment payment = paymentRepository.findByIdForUpdate(id).orElse(null);
        if (payment == null) {
            return null;
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Only pending payments can be updated.");
        }

        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    @Transactional
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
        return paymentRepository.save(payment);
    }
}
