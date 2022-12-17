package com.sivalabs.bookstore.orderservice.cart.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public class CartItemRequestDTO {
    @NotEmpty private String code;

    @Min(0)
    private int quantity;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
