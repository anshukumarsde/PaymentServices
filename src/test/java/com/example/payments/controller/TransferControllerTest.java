package com.example.payments.controller;

import com.example.payments.model.Account;
import com.example.payments.repository.AccountRepository;
import com.example.payments.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferRepository transferRepository;

    @BeforeEach
    void setUpAccounts() {
        transferRepository.deleteAll();
        accountRepository.deleteAll();
        accountRepository.save(new Account("1001", "Alex Morgan", new BigDecimal("100.00")));
        accountRepository.save(new Account("1002", "Jordan Lee", new BigDecimal("50.00")));
    }

    @Test
    void viewAccountsThenTransferAndCheckUpdatedBalances() throws Exception {
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").exists());

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fromAccountNumber":"1001","toAccountNumber":"1002","amount":25.00}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.fromAccountNumber").value("1001"))
                .andExpect(jsonPath("$.amount").value(25.00));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.accountNumber == '1001')].balance").value(75.0))
                .andExpect(jsonPath("$[?(@.accountNumber == '1002')].balance").value(75.0));
        assertEquals(new BigDecimal("75.00"), accountRepository.findById("1001").orElseThrow().getBalance());
        assertEquals(new BigDecimal("75.00"), accountRepository.findById("1002").orElseThrow().getBalance());
        assertEquals(1, transferRepository.count());
    }

    @Test
    void rejectsInsufficientFunds() throws Exception {
        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fromAccountNumber":"1001","toAccountNumber":"1002","amount":101.00}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Insufficient funds in the from account."));

        assertEquals(new BigDecimal("100.00"), accountRepository.findById("1001").orElseThrow().getBalance());
        assertEquals(0, transferRepository.count());
    }
}
