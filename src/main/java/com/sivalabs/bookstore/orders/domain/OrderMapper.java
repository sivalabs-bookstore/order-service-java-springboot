package com.sivalabs.bookstore.orders.domain;

import com.sivalabs.bookstore.orders.api.CreateOrderRequest;
import com.sivalabs.bookstore.orders.domain.entity.Order;
import com.sivalabs.bookstore.orders.domain.entity.OrderItem;
import com.sivalabs.bookstore.orders.domain.entity.OrderStatus;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public Order convertToEntity(CreateOrderRequest orderRequest) {
        Order newOrder = new Order();
        newOrder.setOrderId(UUID.randomUUID().toString());
        newOrder.setStatus(OrderStatus.NEW);
        newOrder.setCustomerName(orderRequest.getCustomer().getName());
        newOrder.setCustomerEmail(orderRequest.getCustomer().getEmail());
        newOrder.setCustomerPhone(orderRequest.getCustomer().getPhone());
        newOrder.setDeliveryAddressLine1(orderRequest.getDeliveryAddress().getAddressLine1());
        newOrder.setDeliveryAddressLine2(orderRequest.getDeliveryAddress().getAddressLine2());
        newOrder.setDeliveryAddressCity(orderRequest.getDeliveryAddress().getCity());
        newOrder.setDeliveryAddressState(orderRequest.getDeliveryAddress().getState());
        newOrder.setDeliveryAddressZipCode(orderRequest.getDeliveryAddress().getZipCode());
        newOrder.setDeliveryAddressCountry(orderRequest.getDeliveryAddress().getCountry());

        Set<OrderItem> orderItems = new HashSet<>();
        for (CreateOrderRequest.LineItem item : orderRequest.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setCode(item.getCode());
            orderItem.setName(item.getName());
            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setOrder(newOrder);
            orderItems.add(orderItem);
        }
        newOrder.setItems(orderItems);
        return newOrder;
    }
}
