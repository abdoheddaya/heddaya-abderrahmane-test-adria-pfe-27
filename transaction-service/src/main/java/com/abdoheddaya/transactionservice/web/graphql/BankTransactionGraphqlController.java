package com.abdoheddaya.transactionservice.web.graphql;

import com.abdoheddaya.transactionservice.domain.BankTransaction;
import com.abdoheddaya.transactionservice.service.BankTransactionService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
public class BankTransactionGraphqlController {

    private final BankTransactionService service;

    public BankTransactionGraphqlController(BankTransactionService service) {
        this.service = service;
    }

    @QueryMapping
    public List<TransactionView> transactions() {
        return service.findAll().stream()
                .map(TransactionView::from)
                .toList();
    }

    @QueryMapping
    public List<TransactionView> transactionsByAccount(@Argument String accountId) {
        return service.findByAccountId(UUID.fromString(accountId)).stream()
                .map(TransactionView::from)
                .toList();
    }

    @MutationMapping
    public TransactionView createTransaction(
            @Argument String accountId,
            @Argument String amount,
            @Argument String currency,
            @Argument String merchantCategory,
            @Argument String country
    ) {
        BankTransaction transaction = service.create(
                UUID.fromString(accountId),
                new BigDecimal(amount),
                currency,
                merchantCategory,
                country
        );
        return TransactionView.from(transaction);
    }

    public record TransactionView(
            String id,
            String accountId,
            String amount,
            String currency,
            String merchantCategory,
            String country,
            String createdAt
    ) {
        public static TransactionView from(BankTransaction transaction) {
            return new TransactionView(
                    transaction.getId().toString(),
                    transaction.getAccountId().toString(),
                    transaction.getAmount().toPlainString(),
                    transaction.getCurrency(),
                    transaction.getMerchantCategory(),
                    transaction.getCountry(),
                    transaction.getCreatedAt().toString()
            );
        }
    }
}