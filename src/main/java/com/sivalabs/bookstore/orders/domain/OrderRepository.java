package com.sivalabs.bookstore.orders.domain;

import com.sivalabs.bookstore.orders.domain.entity.Order;
import com.sivalabs.bookstore.orders.domain.entity.OrderStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderId(String orderId);

    List<Order> findByStatus(OrderStatus status);
}
