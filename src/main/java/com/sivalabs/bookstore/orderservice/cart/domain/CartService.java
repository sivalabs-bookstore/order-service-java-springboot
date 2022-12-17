package com.sivalabs.bookstore.orderservice.cart.domain;

import com.sivalabs.bookstore.orderservice.cart.api.CartItemRequestDTO;
import com.sivalabs.bookstore.orderservice.clients.catalog.Product;
import com.sivalabs.bookstore.orderservice.clients.catalog.ProductNotFoundException;
import com.sivalabs.bookstore.orderservice.clients.catalog.ProductServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CartService {
    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;
    private final ProductServiceClient productServiceClient;

    public CartService(CartRepository cartRepository, ProductServiceClient productServiceClient) {
        this.cartRepository = cartRepository;
        this.productServiceClient = productServiceClient;
    }

    public Cart getCart(String cartId) {
        if (!StringUtils.hasText(cartId)) {
            return cartRepository.save(Cart.withNewId());
        }
        return cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException(cartId));
    }

    public Cart addToCart(String cartId, CartItemRequestDTO cartItemRequest) {
        Cart cart;
        if (!StringUtils.hasText(cartId)) {
            cart = Cart.withNewId();
        } else {
            cart =
                    cartRepository
                            .findById(cartId)
                            .orElseThrow(() -> new CartNotFoundException(cartId));
        }
        log.info("Add code: {} to cart", cartItemRequest.getCode());
        Product product =
                productServiceClient
                        .getProductByCode(cartItemRequest.getCode())
                        .orElseThrow(() -> new ProductNotFoundException(cartItemRequest.getCode()));
        CartItem cartItem =
                new CartItem(
                        product.code(),
                        product.name(),
                        product.description(),
                        product.price(),
                        cartItemRequest.getQuantity() > 0 ? cartItemRequest.getQuantity() : 1);
        cart.addItem(cartItem);
        return cartRepository.save(cart);
    }

    public Cart updateCartItemQuantity(String cartId, CartItemRequestDTO cartItemRequest) {
        Cart cart =
                cartRepository
                        .findById(cartId)
                        .orElseThrow(() -> new CartNotFoundException(cartId));
        log.info(
                "Update quantity: {} for code:{} quantity in cart: {}",
                cartItemRequest.getQuantity(),
                cartItemRequest.getCode(),
                cartId);

        if (cartItemRequest.getQuantity() <= 0) {
            cart.removeItem(cartItemRequest.getCode());
        } else {
            Product product =
                    productServiceClient
                            .getProductByCode(cartItemRequest.getCode())
                            .orElseThrow(
                                    () -> new ProductNotFoundException(cartItemRequest.getCode()));
            cart.updateItemQuantity(product.code(), cartItemRequest.getQuantity());
        }
        return cartRepository.save(cart);
    }

    public Cart removeCartItem(String cartId, String code) {
        Cart cart =
                cartRepository
                        .findById(cartId)
                        .orElseThrow(() -> new CartNotFoundException(cartId));
        log.info("Remove cart line item code: {}", code);
        cart.removeItem(code);
        return cartRepository.save(cart);
    }

    public void removeCart(String cartId) {
        cartRepository.deleteById(cartId);
    }
}
