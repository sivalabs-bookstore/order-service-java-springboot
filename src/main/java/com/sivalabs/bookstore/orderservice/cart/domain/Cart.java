package com.sivalabs.bookstore.orderservice.cart.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("carts")
public class Cart {
    @Id private String id;
    private Set<CartItem> items = new HashSet<>();

    public Cart() {}

    public Cart(String id) {
        this.id = id;
        this.items = new HashSet<>();
    }

    public Cart(String id, Set<CartItem> items) {
        this.id = id;
        this.items = items;
    }

    public static Cart withNewId() {
        return new Cart(UUID.randomUUID().toString());
    }

    public void addItem(CartItem item) {
        for (CartItem cartItem : items) {
            if (cartItem.getCode().equals(item.getCode())) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                return;
            }
        }
        this.items.add(item);
    }

    public void updateItemQuantity(String code, int quantity) {
        for (CartItem cartItem : items) {
            if (cartItem.getCode().equals(code)) {
                cartItem.setQuantity(quantity);
            }
        }
    }

    public void removeItem(String code) {
        CartItem item = null;
        for (CartItem cartItem : items) {
            if (cartItem.getCode().equals(code)) {
                item = cartItem;
                break;
            }
        }
        if (item != null) {
            items.remove(item);
        }
    }

    public void clearItems() {
        items = new HashSet<>();
    }

    public BigDecimal getCartTotal() {
        return items.stream().map(CartItem::getSubTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<CartItem> getItems() {
        return items;
    }

    public void setItems(Set<CartItem> items) {
        this.items = items;
    }
}
