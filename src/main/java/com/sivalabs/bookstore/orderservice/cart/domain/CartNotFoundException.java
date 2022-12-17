package com.sivalabs.bookstore.orderservice.cart.domain;

import java.net.URI;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class CartNotFoundException extends ErrorResponseException {

    public CartNotFoundException(String cartId) {
        super(
                HttpStatus.NOT_FOUND,
                asProblemDetail("Cart with cartId: " + cartId + " not found"),
                null);
    }

    private static ProblemDetail asProblemDetail(String message) {
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message);
        problemDetail.setTitle("Cart Not Found");
        problemDetail.setType(URI.create("https://api.sivalabs-bookstore.com/errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
