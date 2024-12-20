package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.CustomerDetails;
import com.hsl.prompt_be.entities.models.Order;
import com.hsl.prompt_be.entities.models.User;
import com.hsl.prompt_be.entities.requests.KorapayPaymentRequest;
import com.hsl.prompt_be.entities.responses.KorapayCheckoutResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class PaymentService {

    @Value("${korapay.checkout.url}")
    private String korapayCheckoutUrl;

    @Value("${korapay.api.key.secret}")
    private String apiSecretKey;

    private final RestTemplate restTemplate;

    public KorapayCheckoutResponse callKorapayPayoutApi(KorapayPaymentRequest requestBody) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiSecretKey);
        HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<KorapayCheckoutResponse> response = restTemplate.exchange(
                korapayCheckoutUrl,
                HttpMethod.POST,
                request,
                KorapayCheckoutResponse.class
        );

        return response.getBody();
    }

    public KorapayCheckoutResponse initiateOrderPayment(Order order, User user) {

        KorapayPaymentRequest request = KorapayPaymentRequest.builder()
                .amount((int)order.getCharge())
                .redirect_url("https://web.facebook.com/marvel.lous.963/")
                .currency("NGN")
                .reference(order.getOrderId().toString())
                .narration("Order for " + user.getEmail())
                .customer(
                        CustomerDetails.builder()
                                .email(user.getEmail())
                                .name(user.getFirstName() + user.getLastName()).build()
                ).build();

        return callKorapayPayoutApi(request);
    }
}