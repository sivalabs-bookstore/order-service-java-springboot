package com.sivalabs.bookstore.orders;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record ApplicationProperties(
        String paymentServiceUrl,
        String newOrdersTopic,
        String deliveredOrdersTopic,
        String cancelledOrdersTopic
) {
}