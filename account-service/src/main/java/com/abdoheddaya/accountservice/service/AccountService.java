package com.abdoheddaya.accountservice.service;

import com.abdoheddaya.accountservice.domain.Account;
import com.abdoheddaya.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccount(
            String customerReference,
            String currency,
            BigDecimal initialBalance
    ) {
        if (customerReference == null || customerReference.isBlank()) {
            throw new IllegalArgumentException("La référence client est obligatoire");
        }
        if (currency == null || !currency.matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("La devise doit contenir trois lettres");
        }
        if (initialBalance == null || initialBalance.signum() < 0) {
            throw new IllegalArgumentException("Le solde initial ne peut pas être négatif");
        }

        Account account = new Account(
                customerReference.trim(),
                currency.toUpperCase(),
                initialBalance
        );
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Account getAccount(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compte introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}