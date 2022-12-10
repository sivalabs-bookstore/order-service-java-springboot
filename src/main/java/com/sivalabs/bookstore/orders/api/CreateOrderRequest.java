package com.sivalabs.bookstore.orders.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class CreateOrderRequest {

    @NotEmpty(message = "Items cannot be empty.")
    private Set<LineItem> items;

    @Valid private Customer customer;

    @Valid private Address deliveryAddress;

    @Valid private Payment payment;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineItem {
        @NotBlank(message = "Code is required")
        private String code;

        @NotBlank(message = "Name is required")
        private String name;

        @NotNull(message = "Price is required")
        private BigDecimal price;

        @NotNull
        @Min(1)
        private Integer quantity;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Customer {
        @NotBlank(message = "Customer Name is required")
        private String name;

        @NotBlank(message = "Customer email is required")
        @Email
        private String email;

        @NotBlank(message = "Customer Phone number is required")
        private String phone;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        @NotBlank(message = "AddressLine1 is required")
        private String addressLine1;

        private String addressLine2;

        @NotBlank(message = "City is required")
        private String city;

        @NotBlank(message = "State is required")
        private String state;

        @NotBlank(message = "ZipCode is required")
        private String zipCode;

        @NotBlank(message = "Country is required")
        private String country;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payment {
        @NotBlank(message = "Card Number is required")
        private String cardNumber;

        @NotBlank(message = "CVV is required")
        private String cvv;

        @NotNull private Integer expiryMonth;

        @NotNull private Integer expiryYear;
    }
}
