package com.abdoheddaya.accountservice.web.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank String customerReference,
        @NotBlank String currency,
        @NotNull @DecimalMin("0.00") BigDecimal initialBalance
) {
}