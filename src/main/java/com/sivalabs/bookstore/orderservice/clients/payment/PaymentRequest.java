package com.sivalabs.bookstore.orderservice.clients.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotBlank(message = "cardNumber is required") String cardNumber,
        @NotBlank(message = "cvv is required") String cvv,
        @NotNull(message = "expiryMonth is required") Integer expiryMonth,
        @NotNull(message = "expiryYear is required") Integer expiryYear) {}
