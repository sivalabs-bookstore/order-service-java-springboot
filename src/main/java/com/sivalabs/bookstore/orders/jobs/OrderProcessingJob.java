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

    private OrderCreatedEvent buildOrderCreatedEvent(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(order.getOrderId());
        event.setCustomer(new Customer());
        event.getCustomer().setName(order.getCustomerName());
        event.getCustomer().setEmail(order.getCustomerEmail());
        event.getCustomer().setPhone(order.getCustomerPhone());

        event.setDeliveryAddress(new Address());
        event.getDeliveryAddress().setAddressLine1(order.getDeliveryAddressLine1());
        event.getDeliveryAddress().setAddressLine2(order.getDeliveryAddressLine2());
        event.getDeliveryAddress().setCity(order.getDeliveryAddressCity());
        event.getDeliveryAddress().setState(order.getDeliveryAddressState());
        event.getDeliveryAddress().setZipCode(order.getDeliveryAddressZipCode());
        event.getDeliveryAddress().setCountry(order.getDeliveryAddressCountry());

        Set<LineItem> lineItems =
                order.getItems().stream()
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
        event.setItems(lineItems);

        return event;
    }
}
