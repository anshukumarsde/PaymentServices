package com.example.payments.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @GetMapping("/api/hello")
    public String hello() {
        return "Payment service is running";
    }
}
