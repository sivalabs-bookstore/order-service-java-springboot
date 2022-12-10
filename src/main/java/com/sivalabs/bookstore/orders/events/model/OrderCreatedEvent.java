package com.sivalabs.bookstore.orders.events.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private String orderId;
    private Set<LineItem> items;
    private Customer customer;
    private Address deliveryAddress;
}
