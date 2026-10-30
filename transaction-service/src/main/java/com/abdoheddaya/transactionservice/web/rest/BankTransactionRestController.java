package com.abdoheddaya.transactionservice.web.rest;

import com.abdoheddaya.transactionservice.domain.BankTransaction;
import com.abdoheddaya.transactionservice.service.BankTransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class BankTransactionRestController {

    private final BankTransactionService service;

    public BankTransactionRestController(BankTransactionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankTransaction create(@Valid @RequestBody CreateTransactionRequest request) {
        return service.create(
                request.accountId(),
                request.amount(),
                request.currency(),
                request.merchantCategory(),
                request.country()
        );
    }

    @GetMapping
    public List<BankTransaction> findAll() {
        return service.findAll();
    }

    @GetMapping("/account/{accountId}")
    public List<BankTransaction> findByAccount(@PathVariable UUID accountId) {
        return service.findByAccountId(accountId);
    }
}