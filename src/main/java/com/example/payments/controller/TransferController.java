package com.example.payments.controller;

import com.example.payments.model.TransferReceipt;
import com.example.payments.model.TransferRequest;
import com.example.payments.service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferReceipt initiateTransfer(@RequestBody TransferRequest request) {
        return TransferReceipt.from(transferService.initiateTransfer(request));
    }
}
