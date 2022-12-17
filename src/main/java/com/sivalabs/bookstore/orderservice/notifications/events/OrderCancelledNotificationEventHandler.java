package com.sivalabs.bookstore.orderservice.notifications.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivalabs.bookstore.orderservice.common.model.OrderCancelledEvent;
import com.sivalabs.bookstore.orderservice.notifications.NotificationService;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCancelledNotificationEventHandler {
    private static final Logger log =
            org.slf4j.LoggerFactory.getLogger(OrderCancelledNotificationEventHandler.class);
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public OrderCancelledNotificationEventHandler(
            NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.cancelled-orders-topic}", groupId = "notifications")
    public void handle(String payload) {
        try {
            OrderCancelledEvent event = objectMapper.readValue(payload, OrderCancelledEvent.class);
            log.info("Received a OrderCancelledEvent with orderId:{}: ", event.orderId());
            notificationService.sendCancelledNotification(event);
        } catch (JsonProcessingException e) {
            log.error("Error processing OrderCancelledEvent. Payload: {}", payload);
            log.error(e.getMessage(), e);
        }
    }
}
