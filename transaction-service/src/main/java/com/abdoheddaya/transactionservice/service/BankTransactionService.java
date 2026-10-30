package com.abdoheddaya.transactionservice.service;

import com.abdoheddaya.transactionservice.client.AccountSnapshot;
import com.abdoheddaya.transactionservice.domain.BankTransaction;
import com.abdoheddaya.transactionservice.event.TransactionCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import java.time.Instant;
import com.abdoheddaya.transactionservice.repository.BankTransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class BankTransactionService {

    private final BankTransactionRepository repository;
    private final AccountGateway accountGateway;
    private final BigDecimal maxAmount;
    private final ApplicationEventPublisher eventPublisher;

    public BankTransactionService(
            BankTransactionRepository repository,
            AccountGateway accountGateway,
            @Value("${bank.transaction.max-amount}") BigDecimal maxAmount,
            ApplicationEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.accountGateway = accountGateway;
        this.maxAmount = maxAmount;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public BankTransaction create(
            UUID accountId,
            BigDecimal amount,
            String currency,
            String merchantCategory,
            String country
    ) {
        if (accountId == null) {
            throw new IllegalArgumentException("Le compte est obligatoire");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif");
        }
        if (amount.compareTo(maxAmount) > 0) {
            throw new IllegalArgumentException("Le montant dépasse le plafond " + maxAmount);
        }
        if (currency == null || !currency.matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("Devise invalide");
        }
        if (merchantCategory == null || merchantCategory.isBlank()) {
            throw new IllegalArgumentException("Catégorie commerçant obligatoire");
        }
        if (country == null || !country.matches("[A-Za-z]{2}")) {
            throw new IllegalArgumentException("Code pays invalide");
        }

        AccountSnapshot account = accountGateway.getAccount(accountId);

        if (!"ACTIVE".equals(account.status())) {
            throw new IllegalArgumentException("Le compte est bloqué");
        }
        if (!account.currency().equalsIgnoreCase(currency)) {
            throw new IllegalArgumentException("La devise ne correspond pas au compte");
        }
        if (account.balance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Solde insuffisant");
        }

        BankTransaction transaction = repository.saveAndFlush(
                new BankTransaction(
                        accountId,
                        amount,
                        currency.toUpperCase(),
                        merchantCategory.trim(),
                        country.toUpperCase()
                )
        );

        accountGateway.debit(accountId, amount, currency);
        eventPublisher.publishEvent(new TransactionCreatedEvent(
                UUID.randomUUID(),
                "TransactionCreated",
                transaction.getId(),
                accountId,
                amount,
                currency.toUpperCase(),
                Instant.now()
        ));
        return transaction;
    }

    @Transactional(readOnly = true)
    public List<BankTransaction> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<BankTransaction> findByAccountId(UUID accountId) {
        return repository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }
}