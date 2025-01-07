package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.CustomerDetails;
import com.hsl.prompt_be.entities.models.OrderPayment;
import com.hsl.prompt_be.entities.models.Payment;
import com.hsl.prompt_be.entities.models.PaymentRequestType;
import com.hsl.prompt_be.entities.models.User;
import com.hsl.prompt_be.entities.requests.KorapayPaymentRequest;
import com.hsl.prompt_be.entities.responses.KorapayCheckoutResponse;
import com.hsl.prompt_be.exceptions.PaymentNotFoundException;
import com.hsl.prompt_be.repositories.OrderPaymentRepository;
import com.hsl.prompt_be.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PaymentService {

    @Value("${korapay.checkout.url}")
    private String korapayCheckoutUrl;

    @Value("${korapay.api.key.secret}")
    private String apiSecretKey;

    @Value("${korapay.checkout.notification_url}")
    private String orderNotificationUrl;

    @Value("${korapay.checkout.success_callback_url}")
    private String orderSuccessCallbackUrl;

    @Value("${korapay.checkout.deposit_success_callback_url}")
    private String depositSuccessCallbackUrl;

    @Value("${korapay.checkout.deposit_notification_url}")
    private String depositNotificationUrl;

    private final RestTemplate restTemplate;
    private final OrderPaymentRepository orderPaymentRepository;
    private final PaymentRepository paymentRepository;

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

    public KorapayCheckoutResponse initiatePayment(UUID paymentId, int amount, User user, PaymentRequestType paymentType) {

        KorapayPaymentRequest request = KorapayPaymentRequest.builder()
                .amount(amount)
                .currency("NGN")
                .reference(paymentId.toString())
                .customer(
                        CustomerDetails.builder()
                                .email(user.getEmail())
                                .name(user.getFirstName() + user.getLastName()).build()
                ).build();

        if (paymentType.equals(PaymentRequestType.ORDER_PAYMENT)) {

            request.setRedirect_url(orderSuccessCallbackUrl);
            request.setNotification_url(orderNotificationUrl);
            request.setNarration("Order for " + user.getEmail());
        }
        else {

            request.setRedirect_url(depositSuccessCallbackUrl + "/" + paymentId);
            request.setNotification_url(depositNotificationUrl);
            request.setNarration("Order for " + user.getEmail());
        }

        return callKorapayPayoutApi(request);
    }

    public OrderPayment saveOrderPayment(OrderPayment payment) {

        return orderPaymentRepository.save(payment);
    }

    public Payment savePayment(Payment payment) {

        return paymentRepository.save(payment);
    }

    public Payment findPaymentById(UUID paymentId) throws PaymentNotFoundException {

        return paymentRepository.findById(paymentId).orElseThrow(PaymentNotFoundException::new);
    }

    public OrderPayment findOrderPaymentById(UUID paymentId) throws PaymentNotFoundException {

        return orderPaymentRepository.findById(paymentId).orElseThrow(PaymentNotFoundException::new);
    }
}
