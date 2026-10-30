package com.abdoheddaya.accountservice.web.graphql;

import com.abdoheddaya.accountservice.domain.Account;
import com.abdoheddaya.accountservice.service.AccountService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
public class AccountGraphqlController {

    private final AccountService accountService;

    public AccountGraphqlController(AccountService accountService) {
        this.accountService = accountService;
    }

    @QueryMapping
    public List<AccountView> accounts() {
        return accountService.getAllAccounts()
                .stream()
                .map(AccountView::from)
                .toList();
    }

    @QueryMapping
    public AccountView account(@Argument String id) {
        return AccountView.from(accountService.getAccount(UUID.fromString(id)));
    }

    @MutationMapping
    public AccountView createAccount(
            @Argument String customerReference,
            @Argument String currency,
            @Argument String initialBalance
    ) {
        Account account = accountService.createAccount(
                customerReference,
                currency,
                new BigDecimal(initialBalance)
        );
        return AccountView.from(account);
    }

    public record AccountView(
            String id,
            String customerReference,
            String status,
            String currency,
            String balance
    ) {
        public static AccountView from(Account account) {
            return new AccountView(
                    account.getId().toString(),
                    account.getCustomerReference(),
                    account.getStatus().name(),
                    account.getCurrency(),
                    account.getBalance().toPlainString()
            );
        }
    }
}