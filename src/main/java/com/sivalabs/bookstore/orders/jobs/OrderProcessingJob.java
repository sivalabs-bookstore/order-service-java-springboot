package com.sivalabs.bookstore.orders.jobs;

import com.sivalabs.bookstore.orders.ApplicationProperties;
import com.sivalabs.bookstore.orders.domain.OrderService;
import com.sivalabs.bookstore.orders.domain.entity.Order;
import com.sivalabs.bookstore.orders.domain.entity.OrderStatus;
import com.sivalabs.bookstore.orders.events.KafkaHelper;
import com.sivalabs.bookstore.orders.events.model.Address;
import com.sivalabs.bookstore.orders.events.model.Customer;
import com.sivalabs.bookstore.orders.events.model.LineItem;
import com.sivalabs.bookstore.orders.events.model.OrderCreatedEvent;
import com.sivalabs.bookstore.orders.events.model.OrderErrorEvent;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderProcessingJob {
    private final OrderService orderService;
    private final KafkaHelper kafkaHelper;
    private final ApplicationProperties properties;

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
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(order.getOrderId());
        event.setCustomer(getCustomer(order));
        event.setDeliveryAddress(getDeliveryAddress(order));
        event.setItems(getOrderItems(order));

        return event;
    }

    private OrderErrorEvent buildOrderErrorEvent(Order order, String reason) {
        OrderErrorEvent event = new OrderErrorEvent();
        event.setOrderId(order.getOrderId());
        event.setReason(reason);
        event.setCustomer(getCustomer(order));
        event.setDeliveryAddress(getDeliveryAddress(order));
        event.setItems(getOrderItems(order));

        return event;
    }

    private Set<LineItem> getOrderItems(Order order) {
        return order.getItems().stream()
                .map(
                        item -> {
                            LineItem lineItem = new LineItem();
                            lineItem.setCode(item.getCode());
                            lineItem.setName(item.getName());
                            lineItem.setPrice(item.getPrice());
                            lineItem.setQuantity(item.getQuantity());
                            return lineItem;
                        })
                .collect(Collectors.toSet());
    }

    private Customer getCustomer(Order order) {
        Customer customer = new Customer();
        customer.setName(order.getCustomerName());
        customer.setEmail(order.getCustomerEmail());
        customer.setPhone(order.getCustomerPhone());
        return customer;
    }

    private Address getDeliveryAddress(Order order) {
        Address address = new Address();
        address.setAddressLine1(order.getDeliveryAddressLine1());
        address.setAddressLine2(order.getDeliveryAddressLine2());
        address.setCity(order.getDeliveryAddressCity());
        address.setState(order.getDeliveryAddressState());
        address.setZipCode(order.getDeliveryAddressZipCode());
        address.setCountry(order.getDeliveryAddressCountry());
        return address;
    }
}
