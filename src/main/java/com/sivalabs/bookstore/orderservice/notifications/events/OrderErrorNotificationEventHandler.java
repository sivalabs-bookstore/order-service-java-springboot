package com.sivalabs.bookstore.orderservice.notifications.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivalabs.bookstore.orderservice.common.model.OrderErrorEvent;
import com.sivalabs.bookstore.orderservice.notifications.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderErrorNotificationEventHandler {
    private static final Logger log =
            LoggerFactory.getLogger(OrderErrorNotificationEventHandler.class);
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public OrderErrorNotificationEventHandler(
            NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.error-orders-topic}", groupId = "notifications")
    public void handle(String payload) {
        try {
            OrderErrorEvent event = objectMapper.readValue(payload, OrderErrorEvent.class);
            log.info("Received a OrderErrorEvent with orderId:{}: ", event.orderId());
            notificationService.sendErrorNotification(event);
        } catch (RuntimeException | JsonProcessingException e) {
            log.error("Error processing OrderErrorEvent. Payload: {}", payload);
            log.error(e.getMessage(), e);
        }
    }
}
