package com.sivalabs.bookstore.orderservice.clients.payment;

import com.sivalabs.bookstore.orderservice.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentServiceClient {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceClient.class);

    private final RestTemplate restTemplate;
    private final ApplicationProperties properties;

    public PaymentServiceClient(RestTemplate restTemplate, ApplicationProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public PaymentResponse authorize(PaymentRequest request) {
        try {
            HttpEntity<?> httpEntity = new HttpEntity<>(request);
            String url = properties.paymentServiceUrl() + "/api/payments/validate";
            ResponseEntity<PaymentResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, httpEntity, PaymentResponse.class);
            return response.getBody();
        } catch (RuntimeException e) {
            log.error("Error while validation payment: ", e);
            return new PaymentResponse(PaymentResponse.PaymentStatus.REJECTED);
        }
    }
}
