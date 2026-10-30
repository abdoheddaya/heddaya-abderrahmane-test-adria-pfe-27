package com.abdoheddaya.transactionservice.client;

import java.math.BigDecimal;

public record BalanceRequest(BigDecimal amount, String currency) {
}