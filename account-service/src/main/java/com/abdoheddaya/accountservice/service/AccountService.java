package com.abdoheddaya.accountservice.service;

import com.abdoheddaya.accountservice.domain.Account;
import com.abdoheddaya.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.abdoheddaya.accountservice.domain.AccountStatus;

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

    @Transactional
    public Account changeStatus(UUID id, AccountStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }

        Account account = getAccount(id);
        account.setStatus(status);
        return accountRepository.save(account);
    }

    @Transactional
    public Account debit(UUID id, BigDecimal amount, String currency) {
        Account account = lockedActiveAccount(id, amount, currency);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Solde insuffisant");
        }

        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }

    @Transactional
    public Account credit(UUID id, BigDecimal amount, String currency) {
        Account account = lockedActiveAccount(id, amount, currency);
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    private Account lockedActiveAccount(UUID id, BigDecimal amount, String currency) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif");
        }

        Account account = accountRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NoSuchElementException("Compte introuvable : " + id));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Le compte est bloqué");
        }
        if (currency == null || !account.getCurrency().equalsIgnoreCase(currency)) {
            throw new IllegalArgumentException("La devise ne correspond pas au compte");
        }

        return account;
    }

    @Transactional
    public TransferResult transfer(
            UUID sourceId,
            UUID destinationId,
            BigDecimal amount,
            String currency
    ) {
        if (sourceId == null || destinationId == null) {
            throw new IllegalArgumentException("Les deux comptes sont obligatoires");
        }
        if (sourceId.equals(destinationId)) {
            throw new IllegalArgumentException("Les comptes doivent être différents");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif");
        }

        // Verrouiller toujours dans le même ordre évite un blocage entre virements concurrents.
        UUID firstId = sourceId.compareTo(destinationId) < 0 ? sourceId : destinationId;
        UUID secondId = sourceId.compareTo(destinationId) < 0 ? destinationId : sourceId;

        Account first = accountRepository.findByIdForUpdate(firstId)
                .orElseThrow(() -> new NoSuchElementException("Compte introuvable : " + firstId));
        Account second = accountRepository.findByIdForUpdate(secondId)
                .orElseThrow(() -> new NoSuchElementException("Compte introuvable : " + secondId));

        Account source = sourceId.equals(firstId) ? first : second;
        Account destination = destinationId.equals(firstId) ? first : second;

        if (source.getStatus() != AccountStatus.ACTIVE
                || destination.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Les deux comptes doivent être actifs");
        }
        if (currency == null
                || !source.getCurrency().equalsIgnoreCase(currency)
                || !destination.getCurrency().equalsIgnoreCase(currency)) {
            throw new IllegalArgumentException("Les devises ne correspondent pas");
        }
        if (source.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Solde insuffisant");
        }

        source.setBalance(source.getBalance().subtract(amount));
        destination.setBalance(destination.getBalance().add(amount));

        accountRepository.save(source);
        accountRepository.save(destination);
        return new TransferResult(source, destination);
    }

    public record TransferResult(Account source, Account destination) {
    }
}