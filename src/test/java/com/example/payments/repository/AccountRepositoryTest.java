package com.example.payments.repository;

import com.example.payments.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository repository;

    @Test
    void savesAndLocksAccountsInOrder() {
        repository.save(new Account("1002", new BigDecimal("50.00")));
        repository.save(new Account("1001", new BigDecimal("100.00")));

        List<Account> accounts = repository.lockByAccountNumbers(List.of("1001", "1002"));

        assertEquals(List.of("1001", "1002"), accounts.stream().map(Account::getAccountNumber).toList());
    }
}
