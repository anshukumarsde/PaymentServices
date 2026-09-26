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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferRepository transferRepository;

    @BeforeEach
    void clearDatabase() {
        transferRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void createsReadsUpdatesAndDeletesAccount() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"accountNumber":"2001","accountHolderName":"Casey Doe","openingBalance":0.00}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value("2001"))
                .andExpect(jsonPath("$.accountHolderName").value("Casey Doe"));

        mockMvc.perform(get("/api/accounts/2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(0.0));

        mockMvc.perform(put("/api/accounts/2001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"accountHolderName":"Casey Jones"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountHolderName").value("Casey Jones"));

        mockMvc.perform(delete("/api/accounts/2001"))
                .andExpect(status().isNoContent());
        assertFalse(accountRepository.existsById("2001"));
    }

    @Test
    void preventsDeletingAccountWithBalanceOrTransferHistory() throws Exception {
        accountRepository.save(new Account("3001", "Alex Morgan", new BigDecimal("10.00")));
        accountRepository.save(new Account("3002", "Jordan Lee", BigDecimal.ZERO));

        mockMvc.perform(delete("/api/accounts/3001"))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fromAccountNumber":"3001","toAccountNumber":"3002","amount":10.00}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/accounts/3001"))
                .andExpect(status().isConflict());
        assertTrue(accountRepository.existsById("3001"));
    }

    @Test
    void mapsServiceExceptionsThroughCentralHandler() throws Exception {
        mockMvc.perform(get("/api/accounts/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Account number was not found."));

        accountRepository.save(new Account("4001", "Casey Doe", BigDecimal.ZERO));
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"accountNumber":"4001","accountHolderName":"Another Name","openingBalance":0.00}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Account number already exists."));
    }

    @Test
    void mapsMalformedRequestBodiesThroughCentralHandler() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Request body is missing or invalid."));
    }

}
