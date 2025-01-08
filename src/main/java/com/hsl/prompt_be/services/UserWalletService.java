package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.Order;
import com.hsl.prompt_be.entities.models.Payment;
import com.hsl.prompt_be.entities.models.PaymentMethod;
import com.hsl.prompt_be.entities.models.PaymentRequestType;
import com.hsl.prompt_be.entities.models.PaymentStatus;
import com.hsl.prompt_be.entities.models.User;
import com.hsl.prompt_be.entities.models.UserWallet;
import com.hsl.prompt_be.entities.requests.KorapayWebhookRequest;
import com.hsl.prompt_be.entities.responses.AppResponse;
import com.hsl.prompt_be.entities.responses.KorapayCheckoutResponse;
import com.hsl.prompt_be.exceptions.OrderNotFoundException;
import com.hsl.prompt_be.exceptions.PaymentNotFoundException;
import com.hsl.prompt_be.exceptions.UserNotFoundException;
import com.hsl.prompt_be.exceptions.UserWalletNotFoundException;
import com.hsl.prompt_be.exceptions.PrinthubException;
import com.hsl.prompt_be.repositories.OrderRepository;
import com.hsl.prompt_be.repositories.UserWalletRepository;
import com.hsl.prompt_be.utils.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserWalletService {

    private final UserWalletRepository userWalletRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final UserService userService;

    public UserWallet save(UserWallet userWallet) {

        return userWalletRepository.save(userWallet);
    }

    public UserWallet addAmountToUserWallet(UUID userId, int amount) throws UserWalletNotFoundException {

        UserWallet userWallet = userWalletRepository.findById(userId).orElseThrow(UserWalletNotFoundException::new);
        userWallet.setBalance(userWallet.getBalance() + amount);
        userWallet.setUpdatedAt(Instant.now());

        return userWalletRepository.save(userWallet);
    }

    public UserWallet getLoggedInUserWallet() throws PrinthubException {

        return userWalletRepository.findById(UserUtil.getLoggedInUser().getUserId()).get();
    }

    @Transactional
    public KorapayCheckoutResponse initiateDeposit(int amount) throws PrinthubException {

        User user = UserUtil.getLoggedInUser();

        Payment payment = Payment.builder()
                .status(PaymentStatus.pending)
                .method(PaymentMethod.nil)
                .amount(amount)
                .userId(user.getUserId()).build();
        paymentService.savePayment(payment);

        return paymentService.initiatePayment(payment.getPaymentId(), amount, user, PaymentRequestType.WALLET_PAYMENT);
    }

    public AppResponse depositWebhook(KorapayWebhookRequest request) throws PaymentNotFoundException, UserNotFoundException, UserWalletNotFoundException {

        Payment payment = paymentService.findPaymentById(UUID.fromString(request.getData().getReference()));
        payment.setStatus(request.getData().getStatus());

        if (payment.getMethod().equals(PaymentMethod.nil)) {

            payment.setMethod(request.getData().getPayment_method());
            payment.setUpdatedAt(Instant.now());
        }
        paymentService.savePayment(payment);

        if (request.getData().getStatus().equals(PaymentStatus.success)) {

            System.out.println("Payment Success");
            User user = userService.findByUserId(payment.getUserId());

            addAmountToUserWallet(user.getUserId(), request.getData().getAmount());
        }

        return AppResponse.builder()
                .message("Payment reference has been successfully saved")
                .status(HttpStatus.OK).build();
    }

    @Transactional
    public AppResponse payForOrderWithWallet(UUID orderId) throws PrinthubException {

        UserWallet userWallet = getLoggedInUserWallet();
        int balance = userWallet.getBalance();

        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        int orderAmount = order.getCharge();

        if (balance < orderAmount) {
            throw new PrinthubException("Insufficient Funds");
        }

        if (order.isPaid()) {

            return AppResponse.builder()
                    .message("Order has already been paid for")
                    .status(HttpStatus.OK).build();
        }

        order.setPaid(true);
        orderRepository.save(order);

        userWallet.setBalance(balance - orderAmount);
        save(userWallet);

        return AppResponse.builder()
                .message("Payment has been successfully made")
                .status(HttpStatus.OK).build();
    }
}
