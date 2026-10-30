package com.abdoheddaya.transactionservice.service;

import com.abdoheddaya.transactionservice.client.AccountClient;
import com.abdoheddaya.transactionservice.client.AccountSnapshot;
import com.abdoheddaya.transactionservice.client.BalanceRequest;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountGateway {

    private final AccountClient accountClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public AccountGateway(
            AccountClient accountClient,
            CircuitBreakerFactory<?, ?> circuitBreakerFactory
    ) {
        this.accountClient = accountClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public AccountSnapshot getAccount(UUID id) {
        return circuitBreakerFactory.create("account-service")
                .run(() -> accountClient.getAccount(id));
    }

    public AccountSnapshot debit(UUID id, BigDecimal amount, String currency) {
        return circuitBreakerFactory.create("account-service")
                .run(() -> accountClient.debit(id, new BalanceRequest(amount, currency)));
    }
}