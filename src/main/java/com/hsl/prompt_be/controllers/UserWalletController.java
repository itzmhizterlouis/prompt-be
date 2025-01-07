package com.hsl.prompt_be.controllers;

import com.hsl.prompt_be.entities.models.Payment;
import com.hsl.prompt_be.entities.models.UserWallet;
import com.hsl.prompt_be.entities.requests.KorapayWebhookRequest;
import com.hsl.prompt_be.entities.requests.WalletDepositRequest;
import com.hsl.prompt_be.entities.responses.AppResponse;
import com.hsl.prompt_be.entities.responses.KorapayCheckoutResponse;
import com.hsl.prompt_be.exceptions.PaymentNotFoundException;
import com.hsl.prompt_be.exceptions.PrinthubException;
import com.hsl.prompt_be.exceptions.UserNotFoundException;
import com.hsl.prompt_be.exceptions.UserWalletNotFoundException;
import com.hsl.prompt_be.services.PaymentService;
import com.hsl.prompt_be.services.UserWalletService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("user_wallet")
@RequiredArgsConstructor
@RestController
public class UserWalletController {

    private final UserWalletService userWalletService;
    private final PaymentService paymentService;

    @Operation(summary = "Get Logged in user wallet")
    @GetMapping
    public UserWallet getLoggedInUserWallet() throws PrinthubException {

        return userWalletService.getLoggedInUserWallet();
    }

    @PostMapping("initiate/deposit")
    public KorapayCheckoutResponse initiateDeposit(@RequestBody WalletDepositRequest request) throws PrinthubException {

        return userWalletService.initiateDeposit(request.getAmount());
    }

    @Hidden
    @PostMapping("verify/deposit")
    public AppResponse depositWebhook(@RequestBody KorapayWebhookRequest request) throws PaymentNotFoundException, UserNotFoundException, UserWalletNotFoundException {

        return userWalletService.depositWebhook(request);
    }

    @GetMapping("payment/{paymentId}")
    public Payment getPaymentById(@PathVariable UUID paymentId) throws PaymentNotFoundException {

        return paymentService.findPaymentById(paymentId);
    }

    @PostMapping("pay/{orderId}")
    public AppResponse payForOrderWithWallet(@PathVariable UUID orderId) throws PrinthubException {

        return userWalletService.payForOrderWithWallet(orderId);
    }
}
