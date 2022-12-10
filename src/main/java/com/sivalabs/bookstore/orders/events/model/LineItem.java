package com.sivalabs.bookstore.orders.events.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineItem {
    private String code;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
