package com.sivalabs.bookstore.orderservice.notifications.events;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

import com.sivalabs.bookstore.orderservice.ApplicationProperties;
import com.sivalabs.bookstore.orderservice.common.AbstractIntegrationTest;
import com.sivalabs.bookstore.orderservice.common.KafkaHelper;
import com.sivalabs.bookstore.orderservice.common.model.Customer;
import com.sivalabs.bookstore.orderservice.common.model.OrderErrorEvent;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

class OrderErrorNotificationEventHandlerTest extends AbstractIntegrationTest {
    private static final Logger log =
            LoggerFactory.getLogger(OrderErrorNotificationEventHandlerTest.class);
    @Autowired private KafkaHelper kafkaHelper;

    @Autowired private ApplicationProperties properties;

    @Test
    void shouldHandleOrderErrorEvent() {
        Customer customer = new Customer("Siva", "siva@gmail.com", "999999999");
        OrderErrorEvent event =
                new OrderErrorEvent(
                        UUID.randomUUID().toString(), "test error", Set.of(), customer, null);
        log.info("OrderErrorEvent with OrderId:{}", event.orderId());

        kafkaHelper.send(properties.errorOrdersTopic(), event);

        ArgumentCaptor<OrderErrorEvent> captor = ArgumentCaptor.forClass(OrderErrorEvent.class);
        await().atMost(30, SECONDS)
                .untilAsserted(
                        () -> {
                            verify(notificationService).sendErrorNotification(captor.capture());
                            OrderErrorEvent orderErrorEvent = captor.getValue();
                            Assertions.assertThat(orderErrorEvent.orderId())
                                    .isEqualTo(event.orderId());
                        });
    }
}
