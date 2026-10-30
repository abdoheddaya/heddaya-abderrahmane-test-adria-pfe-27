package com.abdoheddaya.transactionservice.web.rest;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionRequest(
        @NotNull UUID accountId,
        @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal amount,
        @NotBlank String currency,
        @NotBlank String merchantCategory,
        @NotBlank String country
) {
}