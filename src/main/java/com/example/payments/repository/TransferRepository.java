package com.example.payments.repository;

import com.example.payments.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, String> {

    boolean existsByFromAccountNumberOrToAccountNumber(String fromAccountNumber, String toAccountNumber);
}
