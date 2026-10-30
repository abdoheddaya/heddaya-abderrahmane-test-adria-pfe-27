package com.abdoheddaya.accountservice.web.rest;

import com.abdoheddaya.accountservice.domain.Account;
import com.abdoheddaya.accountservice.service.AccountService;
import com.abdoheddaya.accountservice.domain.AccountStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountRestController {

    private final AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account create(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.createAccount(
                request.customerReference(),
                request.currency(),
                request.initialBalance()
        );
    }

    @GetMapping
    public List<Account> findAll() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public Account findById(@PathVariable UUID id) {
        return accountService.getAccount(id);
    }

    @PatchMapping("/{id}/status")
    public Account changeStatus(
            @PathVariable UUID id,
            @RequestParam AccountStatus status
    ) {
        return accountService.changeStatus(id, status);
    }

    @PostMapping("/{id}/debit")
    public Account debit(
            @PathVariable UUID id,
            @Valid @RequestBody BalanceChangeRequest request
    ) {
        return accountService.debit(id, request.amount(), request.currency());
    }

    @PostMapping("/{id}/credit")
    public Account credit(
            @PathVariable UUID id,
            @Valid @RequestBody BalanceChangeRequest request
    ) {
        return accountService.credit(id, request.amount(), request.currency());
    }
    @PostMapping("/transfer")
    public AccountService.TransferResult transfer(@Valid @RequestBody TransferRequest request) {
        return accountService.transfer(
                request.sourceAccountId(),
                request.destinationAccountId(),
                request.amount(),
                request.currency()
        );
    }
}