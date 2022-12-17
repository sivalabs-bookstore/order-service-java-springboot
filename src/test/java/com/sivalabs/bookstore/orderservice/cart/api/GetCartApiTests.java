package com.sivalabs.bookstore.orderservice.cart.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.sivalabs.bookstore.orderservice.cart.domain.Cart;
import com.sivalabs.bookstore.orderservice.cart.domain.CartRepository;
import com.sivalabs.bookstore.orderservice.common.AbstractIntegrationTest;
import io.restassured.http.ContentType;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GetCartApiTests extends AbstractIntegrationTest {

    @Autowired private CartRepository cartRepository;

    @Test
    void shouldGetNewCart() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/carts")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("items", hasSize(0));
    }

    @Test
    void shouldGetNotFoundWhenCartIdNotExist() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/carts?cartId=non-existing-cart-id")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldGetExistingCart() {
        String cartId = UUID.randomUUID().toString();
        cartRepository.save(new Cart(cartId, Set.of()));
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/carts?cartId={cartId}", cartId)
                .then()
                .statusCode(200)
                .body("id", is(cartId))
                .body("items", hasSize(0));
    }
}
