package com.sivalabs.bookstore.orderservice.cart.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.sivalabs.bookstore.orderservice.cart.domain.Cart;
import com.sivalabs.bookstore.orderservice.cart.domain.CartItem;
import com.sivalabs.bookstore.orderservice.cart.domain.CartRepository;
import com.sivalabs.bookstore.orderservice.common.AbstractIntegrationTest;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UpdateCartItemApiTests extends AbstractIntegrationTest {

    @Autowired private CartRepository cartRepository;

    @Test
    void shouldUpdateItemQuantity() {
        mockGetProductByCode("P100", "Product 1", BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO);

        String cartId = UUID.randomUUID().toString();
        cartRepository.save(
                new Cart(
                        cartId,
                        Set.of(new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2))));
        given().contentType(ContentType.JSON)
                .body(
                        """
                        {
                            "code": "P100",
                            "quantity": 4
                        }
                        """)
                .when()
                .put("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(1))
                .body("items[0].code", is("P100"))
                .body("items[0].quantity", is(4));
    }

    @Test
    void shouldRemoveItemWhenUpdatedItemQuantityIsZero() {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(
                new Cart(
                        cartId,
                        Set.of(new CartItem("P100", "Product 1", "P100 desc", BigDecimal.TEN, 2))));
        given().contentType(ContentType.JSON)
                .body(
                        """
                        {
                            "code": "P100",
                            "quantity": 0
                        }
                        """)
                .when()
                .put("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(0));
    }
}
