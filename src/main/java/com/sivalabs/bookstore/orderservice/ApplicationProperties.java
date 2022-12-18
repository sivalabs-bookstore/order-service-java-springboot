package com.sivalabs.bookstore.orderservice;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record ApplicationProperties(
        String catalogServiceUrl,
        String paymentServiceUrl,
        String newOrdersTopic,
        String deliveredOrdersTopic,
        String cancelledOrdersTopic,
        String errorOrdersTopic) {}
