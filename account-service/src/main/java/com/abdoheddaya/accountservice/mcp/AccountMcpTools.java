package com.abdoheddaya.accountservice.mcp;

import com.abdoheddaya.accountservice.domain.Account;
import com.abdoheddaya.accountservice.service.AccountService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class AccountMcpTools {
    private final AccountService service;

    public AccountMcpTools(AccountService service) {
        this.service = service;
    }

    @Tool(name = "list_bank_accounts", description = "List all bank accounts and balances")
    public List<Account> listAccounts() {
        return service.getAllAccounts();
    }

    @Tool(name = "get_bank_account", description = "Get the status, currency and balance of a bank account by UUID")
    public Account getAccount(String accountId) {
        return service.getAccount(UUID.fromString(accountId));
    }

    @Tool(name = "transfer_between_accounts", description = "Transfer an amount between two active accounts in the same currency")
    public AccountService.TransferResult transfer(String sourceAccountId, String destinationAccountId, String amount, String currency) {
        return service.transfer(UUID.fromString(sourceAccountId), UUID.fromString(destinationAccountId), new BigDecimal(amount), currency);
    }
}
