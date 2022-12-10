package com.sivalabs.bookstore.orders.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivalabs.bookstore.orders.domain.OrderService;
import com.sivalabs.bookstore.orders.domain.entity.OrderStatus;
import com.sivalabs.bookstore.orders.domain.model.OrderDTO;
import com.sivalabs.bookstore.orders.events.model.OrderDeliveredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDeliveredEventHandler {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.delivered-orders-topic}")
    public void handle(String payload) {
        try {
            OrderDeliveredEvent event = objectMapper.readValue(payload, OrderDeliveredEvent.class);
            log.info("Received a OrderDeliveredEvent with orderId:{}: ", event.getOrderId());
            OrderDTO order = orderService.findOrderByOrderId(event.getOrderId()).orElse(null);
            if (order == null) {
                log.info(
                        "Received invalid OrderDeliveredEvent with orderId:{}: ",
                        event.getOrderId());
                return;
            }
            orderService.updateOrderStatus(order.getOrderId(), OrderStatus.DELIVERED, null);
        } catch (RuntimeException | JsonProcessingException e) {
            log.error("Error processing OrderDeliveredEvent. Payload: {}", payload);
            log.error(e.getMessage(), e);
        }
    }
}
