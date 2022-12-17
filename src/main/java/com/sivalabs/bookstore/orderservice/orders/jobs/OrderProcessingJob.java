package com.sivalabs.bookstore.orderservice.orders.jobs;

import com.sivalabs.bookstore.orderservice.ApplicationProperties;
import com.sivalabs.bookstore.orderservice.common.KafkaHelper;
import com.sivalabs.bookstore.orderservice.common.model.Address;
import com.sivalabs.bookstore.orderservice.common.model.Customer;
import com.sivalabs.bookstore.orderservice.common.model.OrderCreatedEvent;
import com.sivalabs.bookstore.orderservice.common.model.OrderErrorEvent;
import com.sivalabs.bookstore.orderservice.common.model.OrderItem;
import com.sivalabs.bookstore.orderservice.orders.domain.OrderService;
import com.sivalabs.bookstore.orderservice.orders.domain.entity.Order;
import com.sivalabs.bookstore.orderservice.orders.domain.entity.OrderStatus;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessingJob {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(OrderProcessingJob.class);
    private final OrderService orderService;
    private final KafkaHelper kafkaHelper;
    private final ApplicationProperties properties;

    public OrderProcessingJob(
            OrderService orderService, KafkaHelper kafkaHelper, ApplicationProperties properties) {
        this.orderService = orderService;
        this.kafkaHelper = kafkaHelper;
        this.properties = properties;
    }

    @Scheduled(fixedDelay = 60000)
    public void processNewOrders() {
        List<Order> newOrders = orderService.findOrdersByStatus(OrderStatus.NEW);
        for (Order order : newOrders) {
            OrderCreatedEvent orderCreatedEvent = this.buildOrderCreatedEvent(order);
            kafkaHelper.send(properties.newOrdersTopic(), orderCreatedEvent);
            log.info("Published OrderCreatedEvent for orderId:{}", order.getOrderId());
            orderService.updateOrderStatus(order.getOrderId(), OrderStatus.IN_PROCESS, null);
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void processPaymentRejectedOrders() {
        List<Order> orders = orderService.findOrdersByStatus(OrderStatus.PAYMENT_REJECTED);
        for (Order order : orders) {
            OrderErrorEvent orderErrorEvent = this.buildOrderErrorEvent(order, "Payment rejected");
            kafkaHelper.send(properties.errorOrdersTopic(), orderErrorEvent);
            log.info("Published OrderErrorEvent for orderId:{}", order.getOrderId());
        }
    }

    private OrderCreatedEvent buildOrderCreatedEvent(Order order) {
        return new OrderCreatedEvent(
                order.getOrderId(),
                getOrderItems(order),
                getCustomer(order),
                getDeliveryAddress(order));
    }

    private OrderErrorEvent buildOrderErrorEvent(Order order, String reason) {
        return new OrderErrorEvent(
                order.getOrderId(),
                reason,
                getOrderItems(order),
                getCustomer(order),
                getDeliveryAddress(order));
    }

    private Set<OrderItem> getOrderItems(Order order) {
        return order.getItems().stream()
                .map(
                        item ->
                                new OrderItem(
                                        item.getCode(),
                                        item.getName(),
                                        item.getPrice(),
                                        item.getQuantity()))
                .collect(Collectors.toSet());
    }

    private Customer getCustomer(Order order) {
        return new Customer(
                order.getCustomerName(), order.getCustomerEmail(), order.getCustomerPhone());
    }

    private Address getDeliveryAddress(Order order) {
        return new Address(
                order.getDeliveryAddressLine1(),
                order.getDeliveryAddressLine2(),
                order.getDeliveryAddressCity(),
                order.getDeliveryAddressState(),
                order.getDeliveryAddressZipCode(),
                order.getDeliveryAddressCountry());
    }
}
