package com.example.payments.service;

import com.example.payments.model.Payment;
import com.example.payments.model.PaymentRequest;
import com.example.payments.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment create(PaymentRequest request) {
        return paymentRepository.save(new Payment(
                request.sourceAccount(),
                request.destinationAccount(),
                request.amount()
        ));
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> findById(String id) {
        return paymentRepository.findById(id);
    }
}
