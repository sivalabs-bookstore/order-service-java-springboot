package com.sivalabs.bookstore.orderservice.notifications.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivalabs.bookstore.orderservice.common.model.OrderDeliveredEvent;
import com.sivalabs.bookstore.orderservice.notifications.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderDeliveredNotificationEventHandler {
    private static final Logger log =
            LoggerFactory.getLogger(OrderDeliveredNotificationEventHandler.class);
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public OrderDeliveredNotificationEventHandler(
            NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.delivered-orders-topic}", groupId = "notifications")
    public void handle(String payload) {
        try {
            OrderDeliveredEvent event = objectMapper.readValue(payload, OrderDeliveredEvent.class);
            log.info("Received a OrderDeliveredEvent with orderId:{}: ", event.orderId());
            notificationService.sendDeliveredNotification(event);
        } catch (RuntimeException | JsonProcessingException e) {
            log.error("Error processing OrderDeliveredEvent. Payload: {}", payload);
            log.error(e.getMessage(), e);
        }
    }
}
