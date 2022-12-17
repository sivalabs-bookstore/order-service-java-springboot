package com.sivalabs.bookstore.orderservice.notifications.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivalabs.bookstore.orderservice.common.model.OrderCreatedEvent;
import com.sivalabs.bookstore.orderservice.notifications.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedNotificationEventHandler {
    private static final Logger log =
            LoggerFactory.getLogger(OrderCreatedNotificationEventHandler.class);
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public OrderCreatedNotificationEventHandler(
            NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.new-orders-topic}", groupId = "notifications")
    public void handle(String payload) {
        try {
            OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class);
            log.info("Received a OrderCreatedEvent with orderId:{}: ", event.orderId());
            notificationService.sendConfirmationNotification(event);
        } catch (RuntimeException | JsonProcessingException e) {
            log.error("Error processing OrderCreatedEvent. Payload: {}", payload);
            log.error(e.getMessage(), e);
        }
    }
}
