package com.example.payments;

import com.example.payments.model.Account;
import com.example.payments.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@SpringBootApplication
public class PaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }

    @Bean
    CommandLineRunner demoAccounts(AccountRepository accounts) {
        return args -> {
            accounts.save(new Account("1001", new BigDecimal("1000.00")));
            accounts.save(new Account("1002", new BigDecimal("500.00")));
            accounts.save(new Account("1003", new BigDecimal("250.00")));
        };
    }
}
