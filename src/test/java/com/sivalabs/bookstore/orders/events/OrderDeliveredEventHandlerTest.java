package com.sivalabs.bookstore.orders.events;

import static com.sivalabs.bookstore.orders.domain.entity.OrderStatus.DELIVERED;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.sivalabs.bookstore.orders.ApplicationProperties;
import com.sivalabs.bookstore.orders.common.AbstractIntegrationTest;
import com.sivalabs.bookstore.orders.domain.OrderRepository;
import com.sivalabs.bookstore.orders.domain.entity.Order;
import com.sivalabs.bookstore.orders.domain.entity.OrderStatus;
import com.sivalabs.bookstore.orders.events.model.OrderDeliveredEvent;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OrderDeliveredEventHandlerTest extends AbstractIntegrationTest {

    @Autowired private OrderRepository orderRepository;

    @Autowired private KafkaHelper kafkaHelper;

    @Autowired private ApplicationProperties properties;

    @Test
    void shouldHandleOrderDeliveredEvent() {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.NEW);
        order.setCustomerName("Siva");
        order.setCustomerEmail("siva@gmail.com");
        order.setDeliveryAddressLine1("addr line 1");
        order.setDeliveryAddressLine2("addr line 2");
        order.setDeliveryAddressCity("Hyderabad");
        order.setDeliveryAddressState("Telangana");
        order.setDeliveryAddressZipCode("500072");
        order.setDeliveryAddressCountry("India");

        orderRepository.saveAndFlush(order);

        kafkaHelper.send(
                properties.deliveredOrdersTopic(), new OrderDeliveredEvent(order.getOrderId()));

        await().atMost(30, SECONDS)
                .untilAsserted(
                        () -> {
                            Optional<Order> orderOptional =
                                    orderRepository.findByOrderId(order.getOrderId());
                            assertThat(orderOptional).isPresent();
                            assertThat(orderOptional.get().getStatus()).isEqualTo(DELIVERED);
                        });
    }
}
