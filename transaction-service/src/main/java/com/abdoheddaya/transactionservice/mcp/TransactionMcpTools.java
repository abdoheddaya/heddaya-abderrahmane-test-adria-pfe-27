package com.abdoheddaya.transactionservice.mcp;

import com.abdoheddaya.transactionservice.domain.BankTransaction;
import com.abdoheddaya.transactionservice.service.BankTransactionService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class TransactionMcpTools {
    private final BankTransactionService service;

    public TransactionMcpTools(BankTransactionService service) {
        this.service = service;
    }

    @Tool(name = "list_bank_transactions", description = "List all recorded bank transactions")
    public List<BankTransaction> listTransactions() {
        return service.findAll();
    }

    @Tool(name = "list_account_transactions", description = "List recorded transactions for one account UUID")
    public List<BankTransaction> listForAccount(String accountId) {
        return service.findByAccountId(UUID.fromString(accountId));
    }

    @Tool(name = "create_bank_transaction", description = "Record a bank purchase transaction after validating the account and amount")
    public BankTransaction createTransaction(String accountId, String amount, String currency, String merchantCategory, String country) {
        return service.create(UUID.fromString(accountId), new BigDecimal(amount), currency, merchantCategory, country);
    }
}
