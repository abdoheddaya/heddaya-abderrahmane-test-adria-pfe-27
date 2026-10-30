package com.abdoheddaya.transactionservice.repository;

import com.abdoheddaya.transactionservice.domain.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, UUID> {

    List<BankTransaction> findByAccountIdOrderByCreatedAtDesc(UUID accountId);
}