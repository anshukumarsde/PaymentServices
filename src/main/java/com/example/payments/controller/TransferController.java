package com.example.payments.controller;

import com.example.payments.model.Account;
import com.example.payments.model.Transfer;
import com.example.payments.model.TransferRequest;
import com.example.payments.service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        return Map.of("status", "UP", "service", "payment-transfers");
    }

    @GetMapping("/accounts")
    public List<Account> listAccounts() {
        return transferService.listAccounts();
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public Transfer initiateTransfer(@RequestBody TransferRequest request) {
        return transferService.initiateTransfer(request);
    }

    @GetMapping("/transfers")
    public List<Transfer> listTransfers() {
        return transferService.listTransfers();
    }

    @GetMapping("/transfers/{transferId}")
    public ResponseEntity<Transfer> getTransferById(@PathVariable String transferId) {
        return transferService.getTransferById(transferId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTransferRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
