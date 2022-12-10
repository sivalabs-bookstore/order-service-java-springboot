package com.sivalabs.bookstore.orders.domain.model;

import com.sivalabs.bookstore.orders.domain.entity.Order;
import com.sivalabs.bookstore.orders.domain.entity.OrderStatus;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private String orderId;
    private Set<OrderItemDTO> items;
    private Customer customer;
    private Address deliveryAddress;
    private OrderStatus status;
    private String comments;

    public OrderDTO(Order order) {
        this.setId(order.getId());
        this.setOrderId(order.getOrderId());
        this.setCustomer(new Customer());
        this.getCustomer().setName(order.getCustomerName());
        this.getCustomer().setEmail(order.getCustomerEmail());
        this.getCustomer().setPhone(order.getCustomerPhone());

        this.setDeliveryAddress(new Address());
        this.getDeliveryAddress().setAddressLine1(order.getDeliveryAddressLine1());
        this.getDeliveryAddress().setAddressLine2(order.getDeliveryAddressLine2());
        this.getDeliveryAddress().setCity(order.getDeliveryAddressCity());
        this.getDeliveryAddress().setState(order.getDeliveryAddressState());
        this.getDeliveryAddress().setZipCode(order.getDeliveryAddressZipCode());
        this.getDeliveryAddress().setCountry(order.getDeliveryAddressCountry());
        this.setStatus(order.getStatus());
        this.setComments(order.getComments());

        Set<OrderItemDTO> orderItemDTOs =
                order.getItems().stream()
                        .map(
                                item -> {
                                    OrderItemDTO itemDTO = new OrderItemDTO();
                                    itemDTO.setId(item.getId());
                                    itemDTO.setCode(item.getCode());
                                    itemDTO.setName(item.getName());
                                    itemDTO.setPrice(item.getPrice());
                                    itemDTO.setQuantity(item.getQuantity());
                                    return itemDTO;
                                })
                        .collect(Collectors.toSet());
        this.setItems(orderItemDTOs);
    }

    @Getter
    @Setter
    public static class OrderItemDTO {
        private Long id;
        private String code;
        private String name;
        private BigDecimal price;
        private Integer quantity;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Customer {
        private String name;
        private String email;
        private String phone;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String zipCode;
        private String country;
    }
}
