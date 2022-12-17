package com.sivalabs.bookstore.orderservice.orders.domain;

import com.sivalabs.bookstore.orderservice.clients.payment.PaymentRequest;
import com.sivalabs.bookstore.orderservice.clients.payment.PaymentResponse;
import com.sivalabs.bookstore.orderservice.clients.payment.PaymentServiceClient;
import com.sivalabs.bookstore.orderservice.orders.api.CreateOrderRequest;
import com.sivalabs.bookstore.orderservice.orders.domain.entity.Order;
import com.sivalabs.bookstore.orderservice.orders.domain.entity.OrderStatus;
import com.sivalabs.bookstore.orderservice.orders.domain.model.OrderConfirmationDTO;
import com.sivalabs.bookstore.orderservice.orders.domain.model.OrderDTO;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final OrderMapper orderMapper;

    public OrderService(
            OrderRepository orderRepository,
            PaymentServiceClient paymentServiceClient,
            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.paymentServiceClient = paymentServiceClient;
        this.orderMapper = orderMapper;
    }

    public OrderConfirmationDTO createOrder(CreateOrderRequest orderRequest) {
        Order newOrder = orderMapper.convertToEntity(orderRequest);

        CreateOrderRequest.Payment payment = orderRequest.payment();
        PaymentRequest paymentRequest =
                new PaymentRequest(
                        payment.cardNumber(), payment.cvv(),
                        payment.expiryMonth(), payment.expiryYear());
        PaymentResponse paymentResponse = paymentServiceClient.authorize(paymentRequest);
        if (paymentResponse.status() != PaymentResponse.PaymentStatus.ACCEPTED) {
            newOrder.setStatus(OrderStatus.PAYMENT_REJECTED);
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
        return this.orderRepository.findByOrderId(orderId).map(OrderDTO::from);
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
