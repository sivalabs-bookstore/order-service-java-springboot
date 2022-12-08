package com.sivalabs.bookstore.orders.events;

import com.sivalabs.bookstore.orders.domain.OrderService;
import com.sivalabs.bookstore.orders.domain.entity.OrderStatus;
import com.sivalabs.bookstore.orders.domain.model.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDeliveredEventHandler {
    private final OrderService orderService;

    @KafkaListener(topics = "${app.delivered-orders-topic}")
    public void handle(OrderDeliveredEvent event) {
        log.info("Received a OrderDeliveredEvent with orderId:{}: ", event.getOrderId());
        OrderDTO order = orderService.findOrderByOrderId(event.getOrderId()).orElse(null);
        if(order == null) {
            log.info("Received invalid OrderDeliveredEvent with orderId:{}: ", event.getOrderId());
            return;
        }
        orderService.updateOrderStatus(order.getOrderId(), OrderStatus.DELIVERED, null);
    }
}
