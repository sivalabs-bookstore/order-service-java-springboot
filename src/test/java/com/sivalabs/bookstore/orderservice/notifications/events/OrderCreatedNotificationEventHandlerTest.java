package com.sivalabs.bookstore.orderservice.notifications.events;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

import com.sivalabs.bookstore.orderservice.ApplicationProperties;
import com.sivalabs.bookstore.orderservice.common.AbstractIntegrationTest;
import com.sivalabs.bookstore.orderservice.common.KafkaHelper;
import com.sivalabs.bookstore.orderservice.common.model.Customer;
import com.sivalabs.bookstore.orderservice.common.model.OrderCreatedEvent;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

class OrderCreatedNotificationEventHandlerTest extends AbstractIntegrationTest {

    private static final Logger log =
            LoggerFactory.getLogger(OrderCreatedNotificationEventHandlerTest.class);
    @Autowired private KafkaHelper kafkaHelper;

    @Autowired private ApplicationProperties properties;

    @Test
    void shouldHandleOrderCreatedEvent() {
        Customer customer = new Customer("Siva", "siva@gmail.com", "999999999");
        OrderCreatedEvent event =
                new OrderCreatedEvent(UUID.randomUUID().toString(), Set.of(), customer, null);
        log.info("Delivered OrderId:{}", event.orderId());

        kafkaHelper.send(properties.newOrdersTopic(), event);

        ArgumentCaptor<OrderCreatedEvent> captor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        await().atMost(30, SECONDS)
                .untilAsserted(
                        () -> {
                            verify(notificationService)
                                    .sendConfirmationNotification(captor.capture());
                            OrderCreatedEvent orderCreatedEvent = captor.getValue();
                            assertThat(orderCreatedEvent.orderId()).isEqualTo(event.orderId());
                        });
    }
}
