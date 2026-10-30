package com.abdoheddaya.transactionservice.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bank_transactions")
public class BankTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private String merchantCategory;

    @Column(nullable = false, length = 2)
    private String country;

    @Column(nullable = false)
    private Instant createdAt;

    protected BankTransaction() {
        // Requis par JPA
    }

    public BankTransaction(
            UUID accountId,
            BigDecimal amount,
            String currency,
            String merchantCategory,
            String country
    ) {
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
        this.merchantCategory = merchantCategory;
        this.country = country;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getMerchantCategory() {
        return merchantCategory;
    }

    public String getCountry() {
        return country;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}