package com.hsl.prompt_be.controllers;

import com.hsl.prompt_be.entities.models.UserWallet;
import com.hsl.prompt_be.exceptions.PrinthubException;
import com.hsl.prompt_be.services.UserWalletService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("user_wallet")
@RequiredArgsConstructor
@RestController
public class UserWalletController {

    private final UserWalletService userWalletService;

    @Operation(summary = "Get Logged in user wallet")
    @GetMapping
    public UserWallet getLoggedInUserWallet() throws PrinthubException {

        return userWalletService.getLoggedInUserWallet();
    }
}
