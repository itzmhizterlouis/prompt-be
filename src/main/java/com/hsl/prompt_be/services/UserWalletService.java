package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.Payment;
import com.hsl.prompt_be.entities.models.PaymentMethod;
import com.hsl.prompt_be.entities.models.PaymentRequestType;
import com.hsl.prompt_be.entities.models.PaymentStatus;
import com.hsl.prompt_be.entities.models.User;
import com.hsl.prompt_be.entities.models.UserWallet;
import com.hsl.prompt_be.entities.requests.EmailDetails;
import com.hsl.prompt_be.entities.requests.KorapayWebhookRequest;
import com.hsl.prompt_be.entities.responses.AppResponse;
import com.hsl.prompt_be.entities.responses.KorapayCheckoutResponse;
import com.hsl.prompt_be.exceptions.PaymentNotFoundException;
import com.hsl.prompt_be.exceptions.UserNotFoundException;
import com.hsl.prompt_be.exceptions.UserWalletNotFoundException;
import com.hsl.prompt_be.exceptions.PrinthubException;
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
    private final PaymentService paymentService;
    private final UserService userService;
    private final EmailService emailService;

    public UserWallet save(UserWallet userWallet) {

        return userWalletRepository.save(userWallet);
    }

    public UserWallet findByUserId(UUID userId) throws UserWalletNotFoundException {

        return userWalletRepository.findById(userId).orElseThrow(UserWalletNotFoundException::new);
    }

    public UserWallet addAmountToUserWallet(UUID userId, int amount) throws UserWalletNotFoundException {

        UserWallet userWallet = userWalletRepository.findById(userId).orElseThrow(UserWalletNotFoundException::new);
        userWallet.setBalance(userWallet.getBalance() + amount);
        userWallet.setUpdatedAt(Instant.now());

        System.out.println("Entered here");

        return userWalletRepository.save(userWallet);
    }

    public UserWallet getLoggedInUserWallet() throws PrinthubException {

        return userWalletRepository.findById(UserUtil.getLoggedInUser().getUserId()).get();
    }

    @Transactional
    public KorapayCheckoutResponse initiateDeposit(int amount) throws PrinthubException {

        User user = UserUtil.getLoggedInUser();

        if (amount < 100) {

            throw new PrinthubException("Cannot Deposit amount less than 100 naira");
        }

        Payment payment = Payment.builder()
                .status(PaymentStatus.pending)
                .method(PaymentMethod.nil)
                .amount(amount)
                .fee(calculateDepositCharge(amount))
                .userId(user.getUserId()).build();
        paymentService.savePayment(payment);

        return paymentService.initiatePayment(payment.getPaymentId(), amount + payment.getFee(), user, PaymentRequestType.WALLET_PAYMENT); // amount here
    }

    public AppResponse korapayWebhook(KorapayWebhookRequest request) throws PaymentNotFoundException, UserNotFoundException, UserWalletNotFoundException {

        Payment payment = paymentService.findPaymentById(UUID.fromString(request.getData().getReference()));
        payment.setStatus(request.getData().getStatus());

        if (payment.getMethod().equals(PaymentMethod.nil)) {

            payment.setMethod(request.getData().getPayment_method());
            payment.setUpdatedAt(Instant.now());
        }
        payment.setMethod(PaymentMethod.nil);
        paymentService.savePayment(payment);
        User user = userService.findByUserId(payment.getUserId());

        System.out.println("Korapay Webhook Entry");

        if (request.getEvent().contains("charge")) {
            if (request.getData().getStatus().equals(PaymentStatus.success)) {

                System.out.println("Deposit Success");
                addAmountToUserWallet(user.getUserId(), request.getData().getAmount() - payment.getFee());

                emailService.sendSimpleMail(
                        EmailDetails.builder()
                                .recipient(user.getEmail())
                                .messageBody("You made a deposit of " + payment.getAmount() + " to your Printhub wallet")
                                .subject("PrintHub Wallet Deposit").build()
                );
            }
            else {
                System.out.println("Deposit Failed");
                emailService.sendSimpleMail(
                        EmailDetails.builder()
                                .recipient(user.getEmail())
                                .messageBody("Deposit of " + payment.getAmount() + " failed")
                                .subject("PrintHub Wallet Deposit").build()
                );
            }
        }
        else {
            if (request.getData().getStatus().equals(PaymentStatus.success)) {

                System.out.println("Transfer Success");

                addAmountToUserWallet(user.getUserId(), -payment.getAmount());
                emailService.sendSimpleMail(
                        EmailDetails.builder()
                                .recipient(user.getEmail())
                                .messageBody("You successfully withdrew " + payment.getAmount() + " from your Printhub wallet")
                                .subject("PrintHub Wallet Deposit").build()
                );
            }
            else {

                addAmountToUserWallet(user.getUserId(), -payment.getAmount()); //TODO: Remove this for failed transfer

                System.out.println("Transfer failed");
                emailService.sendSimpleMail(
                        EmailDetails.builder()
                                .recipient(user.getEmail())
                                .messageBody("Withdrawal of " + payment.getAmount() + " failed")
                                .subject("PrintHub Wallet Deposit").build()
                );
            }
        }

        System.out.println("End of webhook");

        return AppResponse.builder()
                .message("Payment reference has been successfully saved")
                .status(HttpStatus.OK).build();
    }

    public int calculateDepositCharge(int amount) {

        if (amount <= 500) {
            return 10;
        }
        if (amount <= 1000) {
            return 30;
        }
        else if (amount <= 3000) {
            return 50;
        }
        else if (amount <= 5000) {
            return 100;
        }
        return (int) (0.02 * amount);
    }
}
