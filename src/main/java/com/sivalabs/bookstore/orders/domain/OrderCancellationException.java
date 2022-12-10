package com.sivalabs.bookstore.orders.domain;

import java.net.URI;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class OrderCancellationException extends ErrorResponseException {

    public OrderCancellationException(String orderId, String reason) {
        super(
                HttpStatus.BAD_REQUEST,
                asProblemDetail(
                        "Order with orderId " + orderId + " can't be cancelled. Reason: " + reason),
                null);
    }

    private static ProblemDetail asProblemDetail(String message) {
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message);
        problemDetail.setTitle("Order Not Found");
        problemDetail.setType(URI.create("https://api.sivalabs-bookstore.com/errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
