package com.sivalabs.bookstore.orders.clients.payment;

import com.sivalabs.bookstore.orders.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceClient {
    private final RestTemplate restTemplate;
    private final ApplicationProperties properties;

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
