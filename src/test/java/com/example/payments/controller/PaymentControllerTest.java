package com.example.payments.controller;

import com.example.payments.model.Payment;
import com.example.payments.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void clearPayments() {
        paymentRepository.deleteAll();
    }

    @Test
    void createsListsAndFindsPayment() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sourceAccount":"source","destinationAccount":"destination","amount":12.50}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sourceAccount").value("source"))
                .andReturn();

        Payment payment = paymentRepository.findAll().get(0);
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(payment.getId()));
        mockMvc.perform(get("/api/payments/{id}", payment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(payment.getId()));
    }

    @Test
    void returnsNotFoundForUnknownPayment() throws Exception {
        mockMvc.perform(get("/api/payments/missing"))
                .andExpect(status().isNotFound());
    }
}
