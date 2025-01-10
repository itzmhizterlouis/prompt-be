package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.CustomerDetails;
import com.hsl.prompt_be.entities.models.OrderPayment;
import com.hsl.prompt_be.entities.models.PayType;
import com.hsl.prompt_be.entities.models.Payment;
import com.hsl.prompt_be.entities.models.PaymentMethod;
import com.hsl.prompt_be.entities.models.PaymentRequestType;
import com.hsl.prompt_be.entities.models.PaymentStatus;
import com.hsl.prompt_be.entities.models.Printer;
import com.hsl.prompt_be.entities.models.User;
import com.hsl.prompt_be.entities.models.UserWallet;
import com.hsl.prompt_be.entities.requests.KorapayBank;
import com.hsl.prompt_be.entities.requests.KorapayPaymentRequest;
import com.hsl.prompt_be.entities.requests.KorapayPayoutDestination;
import com.hsl.prompt_be.entities.requests.KorapayPayoutRequest;
import com.hsl.prompt_be.entities.responses.KorapayCheckoutResponse;
import com.hsl.prompt_be.entities.responses.KorapayPayoutResponse;
import com.hsl.prompt_be.exceptions.BankNotSupportedException;
import com.hsl.prompt_be.exceptions.PaymentNotFoundException;
import com.hsl.prompt_be.exceptions.PrinthubException;
import com.hsl.prompt_be.repositories.OrderPaymentRepository;
import com.hsl.prompt_be.repositories.PaymentRepository;
import com.hsl.prompt_be.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
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

    @Value("${korapay.payout}")
    private String korapayPayoutUrl;

    private final RestTemplate restTemplate;
    private final OrderPaymentRepository orderPaymentRepository;
    private final PaymentRepository paymentRepository;

    public KorapayCheckoutResponse callKorapayCheckoutApi(KorapayPaymentRequest requestBody) {

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

    public KorapayPayoutResponse callKorapayPayoutApi(Printer printer, UserWallet userWallet) throws PrinthubException {

        String bankCode = getCodeByBankName(printer.getBankName());
        if (bankCode == null) {
            throw new BankNotSupportedException();
        }

        Payment payment = Payment.builder()
                .status(PaymentStatus.pending)
                .method(PaymentMethod.nil)
                .paymentType(PayType.WITHDRAWAL)
                .amount(userWallet.getBalance())
                .userId(userWallet.getUserId()).build();

        savePayment(payment);

        User loggedInUser = UserUtil.getLoggedInUser();

        KorapayPayoutRequest requestBody = KorapayPayoutRequest.builder()
                .reference(payment.getPaymentId().toString())
                .destination(
                        KorapayPayoutDestination.builder()
                                .type("bank_account")
                                .amount(String.valueOf(payment.getAmount()))
                                .currency("NGN")
                                .narration("Vendor Withdrawal")
                                .bank_account(
                                        KorapayBank.builder()
                                                .bank(bankCode)
                                                .account(String.valueOf(printer.getAccountNumber())).build()
                                )
                                .customer(
                                        CustomerDetails.builder()
                                                .email(loggedInUser.getEmail())
                                                .name(loggedInUser.getFirstName() + " " + loggedInUser.getLastName()).build()
                                ).build()
                ).build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiSecretKey);
        HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<KorapayPayoutResponse> response = restTemplate.exchange(
                korapayPayoutUrl,
                HttpMethod.POST,
                request,
                KorapayPayoutResponse.class
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

            request.setRedirect_url(depositSuccessCallbackUrl);
            request.setNotification_url(depositNotificationUrl);
            request.setNarration("Order for " + user.getEmail());
        }

        return callKorapayCheckoutApi(request);
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

    public String getCodeByBankName(String bankName) {

        Map<String, String> bankToCode = new HashMap<>();

        bankToCode.putAll(Map.of(
                "044", "Access Bank Nigeria Plc",
                "063", "Diamond Bank Plc",
                "050", "Ecobank Nigeria",
                "084", "Enterprise Bank Plc",
                "070", "Fidelity Bank Plc",
                "011", "First Bank of Nigeria Plc",
                "214", "First City Monument Bank",
                "058", "Guaranty Trust Bank Plc",
                "030", "Heritage Banking Company Ltd"
        ));

        bankToCode.putAll(Map.of(
                "301", "Jaiz Bank",
                "082", "Keystone Bank Ltd",
                "014", "Mainstreet Bank Plc",
                "076", "Skye Bank Plc",
                "039", "Stanbic IBTC Plc",
                "232", "Sterling Bank Plc",
                "032", "Union Bank Nigeria Plc",
                "033", "United Bank for Africa Plc"
        ));

        bankToCode.putAll(Map.of(
                "215", "Unity Bank Plc",
                "035", "WEMA Bank Plc",
                "057", "Zenith Bank International"
        ));

        for (Map.Entry<String, String> entry : bankToCode.entrySet()) {
            if (entry.getValue().equals(bankName)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
