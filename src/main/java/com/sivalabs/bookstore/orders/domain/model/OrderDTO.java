package com.sivalabs.bookstore.orders.domain.model;

import com.sivalabs.bookstore.orders.domain.entity.Order;
import com.sivalabs.bookstore.orders.domain.entity.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private String orderId;
    private Set<OrderItemDTO> items;
    private String customerName;
    private String customerEmail;
    private String deliveryAddressLine1;
    private String deliveryAddressLine2;
    private String deliveryAddressCity;
    private String deliveryAddressState;
    private String deliveryAddressZipCode;
    private String deliveryAddressCountry;
    private OrderStatus status;
    private String comments;

    public OrderDTO(Order order) {
        this.setId(order.getId());
        this.setOrderId(order.getOrderId());
        this.setCustomerName(order.getCustomerName());
        this.setCustomerEmail(order.getCustomerEmail());
        this.setDeliveryAddressLine1(order.getDeliveryAddressLine1());
        this.setDeliveryAddressLine2(order.getDeliveryAddressLine2());
        this.setDeliveryAddressCity(order.getDeliveryAddressCity());
        this.setDeliveryAddressState(order.getDeliveryAddressState());
        this.setDeliveryAddressZipCode(order.getDeliveryAddressZipCode());
        this.setDeliveryAddressCountry(order.getDeliveryAddressCountry());
        this.setStatus(order.getStatus());
        this.setComments(order.getComments());

        Set<OrderItemDTO> orderItemDTOs = order.getItems().stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setIsbn(item.getIsbn());
            itemDTO.setTitle(item.getTitle());
            itemDTO.setPrice(item.getPrice());
            itemDTO.setQuantity(itemDTO.getQuantity());
            return itemDTO;
        }).collect(Collectors.toSet());
        this.setItems(orderItemDTOs);
    }

    @Getter
    @Setter
    public static class OrderItemDTO {
        private Long id;
        private String isbn;
        private String title;
        private BigDecimal price;
        private Integer quantity;
    }
}
