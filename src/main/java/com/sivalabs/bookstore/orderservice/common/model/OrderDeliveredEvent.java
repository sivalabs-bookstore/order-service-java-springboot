package com.sivalabs.bookstore.orderservice.common.model;

import java.util.Set;

public record OrderDeliveredEvent(
        String orderId, Set<OrderItem> items, Customer customer, Address deliveryAddress) {}
