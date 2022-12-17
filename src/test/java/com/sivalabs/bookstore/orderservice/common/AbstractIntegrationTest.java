package com.sivalabs.bookstore.orderservice.common;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

import com.sivalabs.bookstore.orderservice.notifications.NotificationService;
import io.restassured.RestAssured;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine");
    protected static final GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:7.0.5-alpine"))
                    .withExposedPorts(6379);
    protected static final KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));
    protected static final MockServerContainer mockServer =
            new MockServerContainer(
                    DockerImageName.parse("jamesdbloom/mockserver:mockserver-5.13.2"));

    protected static MockServerClient mockServerClient;

    @MockBean protected NotificationService notificationService;

    @BeforeAll
    static void beforeAll() {
        Startables.deepStart(postgres, redis, kafka, mockServer).join();
    }

    @LocalServerPort private Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        mockServerClient.reset();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("app.product-service-url", mockServer::getEndpoint);
        registry.add("app.payment-service-url", mockServer::getEndpoint);
        mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    }

    protected static void mockPaymentValidationRequest(String status) {
        mockServerClient
                .when(request().withMethod("POST").withPath("/api/payments/validate"))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(
                                        new Header(
                                                "Content-Type", "application/json; charset=utf-8"))
                                .withBody(
                                        json(
                                                """
                                            {
                                                "status": "%s"
                                            }
                                        """
                                                        .formatted(status))));
    }

    protected static void mockGetProductByCode(
            String code, String name, BigDecimal price, BigDecimal discount, BigDecimal salePrice) {
        mockServerClient
                .when(request().withMethod("GET").withPath("/api/products/" + code))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(
                                        new Header(
                                                "Content-Type", "application/json; charset=utf-8"))
                                .withBody(
                                        json(
                                                """
                                            {
                                                "code": "%s",
                                                "name": "%s",
                                                "price": %f,
                                                "discount": %f,
                                                "salePrice": %f
                                            }
                                        """
                                                        .formatted(
                                                                code,
                                                                name,
                                                                price.doubleValue(),
                                                                discount.doubleValue(),
                                                                salePrice.doubleValue()))));
    }
}
