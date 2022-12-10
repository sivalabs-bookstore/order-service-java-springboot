package com.sivalabs.bookstore.orders.domain;

import com.sivalabs.bookstore.orders.api.CreateOrderRequest;
import com.sivalabs.bookstore.orders.clients.payment.PaymentRequest;
import com.sivalabs.bookstore.orders.clients.payment.PaymentResponse;
import com.sivalabs.bookstore.orders.clients.payment.PaymentServiceClient;
import com.sivalabs.bookstore.orders.domain.entity.Order;
import com.sivalabs.bookstore.orders.domain.entity.OrderStatus;
import com.sivalabs.bookstore.orders.domain.model.OrderConfirmationDTO;
import com.sivalabs.bookstore.orders.domain.model.OrderDTO;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final OrderMapper orderMapper;

    public OrderConfirmationDTO createOrder(CreateOrderRequest orderRequest) {
        Order newOrder = orderMapper.convertToEntity(orderRequest);

        CreateOrderRequest.Payment payment = orderRequest.getPayment();
        PaymentRequest paymentRequest =
                new PaymentRequest(
                        payment.getCardNumber(), payment.getCvv(),
                        payment.getExpiryMonth(), payment.getExpiryYear());
        PaymentResponse paymentResponse = paymentServiceClient.authorize(paymentRequest);
        if (paymentResponse.getStatus() != PaymentResponse.PaymentStatus.ACCEPTED) {
            newOrder.setStatus(OrderStatus.ERROR);
            newOrder.setComments("Payment rejected");
        }
        Order savedOrder = this.orderRepository.save(newOrder);
        log.info("Created Order with orderId=" + savedOrder.getOrderId());
        return new OrderConfirmationDTO(savedOrder.getOrderId(), savedOrder.getStatus());
    }

    public void cancelOrder(String orderId) {
        log.info("Cancel order with orderId: {}", orderId);
        Order order =
                this.orderRepository
                        .findByOrderId(orderId)
                        .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new OrderCancellationException(order.getOrderId(), "Order is already delivered");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public Optional<OrderDTO> findOrderByOrderId(String orderId) {
        return this.orderRepository.findByOrderId(orderId).map(OrderDTO::new);
    }

    public List<Order> findOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public void updateOrderStatus(String orderId, OrderStatus status, String comments) {
        Order order =
                orderRepository
                        .findByOrderId(orderId)
                        .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setStatus(status);
        order.setComments(comments);
        orderRepository.save(order);
    }
}
