package com.sivalabs.bookstore.orders.clients.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotBlank(message = "cardNumber is required")
    private String cardNumber;
    @NotBlank(message = "cvv is required")
    private String cvv;
    @NotNull(message = "expiryMonth is required")
    private Integer expiryMonth;
    @NotNull(message = "expiryYear is required")
    private Integer expiryYear;
}
