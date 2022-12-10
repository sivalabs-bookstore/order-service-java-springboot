package com.sivalabs.bookstore.orders.domain.model;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDTO {
    private Long id;
    private String code;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
