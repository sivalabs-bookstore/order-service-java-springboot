package com.sivalabs.bookstore.orderservice.clients.payment;

public record PaymentResponse(PaymentStatus status) {
    public enum PaymentStatus {
        ACCEPTED,
        REJECTED
    }
}
