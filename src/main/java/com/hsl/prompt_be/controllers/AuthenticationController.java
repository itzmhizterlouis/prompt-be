package com.hsl.prompt_be.controllers;

import com.hsl.prompt_be.entities.models.Printer;
import com.hsl.prompt_be.entities.requests.LoginRequest;
import com.hsl.prompt_be.entities.requests.OtpRequest;
import com.hsl.prompt_be.entities.requests.PrinterRequest;
import com.hsl.prompt_be.entities.requests.UserRequest;
import com.hsl.prompt_be.entities.responses.GenericResponse;
import com.hsl.prompt_be.entities.responses.LoginResponse;
import com.hsl.prompt_be.entities.responses.UserResponse;
import com.hsl.prompt_be.exceptions.OtpNotFoundException;
import com.hsl.prompt_be.exceptions.PrinthubException;
import com.hsl.prompt_be.exceptions.UserNotFoundException;
import com.hsl.prompt_be.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("authentication")
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "sign up endpoint")
    @PostMapping("signup")
    public UserResponse register(@RequestBody UserRequest request) {

        return authenticationService.signUp(request);
    }

    @Operation(summary = "login endpoint", description = "please when isEmailVerified is false do not authenticate user")
    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginRequest request) throws PrinthubException {

        return authenticationService.login(request);
    }

    @Operation(summary = "generate otp endpoint",description = "unprotected route for generating otp")
    @GetMapping("generate-otp/{email}")
    public GenericResponse generateOtp(@PathVariable String email) throws PrinthubException {

        return authenticationService.sendOtpToMail(email);
    }

    @Operation(summary = "verify otp endpoint")
    @PutMapping("verify-otp")
    public UserResponse verifyOtp(@RequestBody OtpRequest request) throws UserNotFoundException, OtpNotFoundException {

        return authenticationService.verifyOtp(request);
    }

    @Operation(summary = "get logged in user endpoint")
    @GetMapping("logged-in-user")
    public UserResponse getLoggedInUser() throws PrinthubException {

        return authenticationService.getLoggedInUser();
    }

    @Operation(summary = "create printer endpoint", description = "unprotected route but make sure to pass in the user id")
    @PostMapping("{userId}/printers")
    public Printer createPrinter(@RequestBody PrinterRequest request, @PathVariable UUID userId) throws UserNotFoundException {

        return authenticationService.createPrinter(request, userId);
    }
}
