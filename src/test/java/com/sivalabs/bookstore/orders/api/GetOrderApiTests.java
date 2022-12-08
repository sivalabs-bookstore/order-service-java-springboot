package com.sivalabs.bookstore.orders.api;

import com.sivalabs.bookstore.orders.common.AbstractIntegrationTest;
import com.sivalabs.bookstore.orders.domain.OrderService;
import com.sivalabs.bookstore.orders.domain.model.OrderConfirmationDTO;
import com.sivalabs.bookstore.orders.domain.model.OrderDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class GetOrderApiTests extends AbstractIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Test
    void shouldCreateOrderSuccessfully() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        CreateOrderRequest.Customer customer = new CreateOrderRequest.Customer();
        customer.setName("Siva");
        customer.setEmail("siva@gmail.com");
        customer.setPhone("99999999999");
        createOrderRequest.setCustomer(customer);

        CreateOrderRequest.Address address = new CreateOrderRequest.Address();
        address.setAddressLine1("addr line 1");
        address.setAddressLine2("addr line 2");
        address.setCity("Hyderabad");
        address.setState("Telangana");
        address.setZipCode("500072");
        address.setCountry("India");
        createOrderRequest.setDeliveryAddress(address);

        CreateOrderRequest.Payment payment = new CreateOrderRequest.Payment();
        payment.setCardNumber("1234123412341234");
        payment.setCvv("123");
        payment.setExpiryMonth(10);
        payment.setExpiryYear(2025);
        createOrderRequest.setPayment(payment);

        createOrderRequest.setItems(Set.of(
                new CreateOrderRequest.LineItem("P100", "Product 1", BigDecimal.TEN, 1)
        ));
        OrderConfirmationDTO orderConfirmationDTO = orderService.createOrder(createOrderRequest);

        OrderDTO orderDTO = given()
                .when()
                .get("/api/orders/{orderId}", orderConfirmationDTO.getOrderId())
                .then()
                .statusCode(200)
                .extract().body().as(OrderDTO.class);

        assertThat(orderDTO.getOrderId()).isEqualTo(orderConfirmationDTO.getOrderId());
        assertThat(orderDTO.getItems()).hasSize(1);
    }

    @Test
    void shouldReturnNotFoundWhenOrderIdNotExist() {
        given()
                .when()
                .get("/api/orders/{orderId}", "non-existing-order-id")
                .then()
                .statusCode(404);
    }
}