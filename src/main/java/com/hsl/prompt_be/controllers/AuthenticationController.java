package com.hsl.prompt_be.controllers;

import com.hsl.prompt_be.entities.models.Printer;
import com.hsl.prompt_be.entities.requests.LoginRequest;
import com.hsl.prompt_be.entities.requests.OtpRequest;
import com.hsl.prompt_be.entities.requests.PrinterRequest;
import com.hsl.prompt_be.entities.requests.UserRequest;
import com.hsl.prompt_be.entities.responses.GenericResponse;
import com.hsl.prompt_be.entities.responses.LoginResponse;
import com.hsl.prompt_be.entities.responses.UserResponse;
import com.hsl.prompt_be.exception.OtpNotFoundException;
import com.hsl.prompt_be.exception.PrinthubException;
import com.hsl.prompt_be.exception.UserNotFoundException;
import com.hsl.prompt_be.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("authentication/")
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("signup")
    public UserResponse register(@RequestBody UserRequest request) {

        return authenticationService.signUp(request);
    }

    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        return authenticationService.login(request);
    }

    @PostMapping("generate-otp")
    public GenericResponse generateOtp() throws PrinthubException {

        return authenticationService.sendOtpToMail();
    }

    @PutMapping("verify-otp")
    public UserResponse verifyOtp(@RequestBody OtpRequest request) throws UserNotFoundException, OtpNotFoundException {

        return authenticationService.verifyOtp(request);
    }

    @GetMapping("logged-in-user")
    public UserResponse getLoggedInUser() throws PrinthubException {

        return authenticationService.getLoggedInUser();
    }

    @PostMapping("printer")
    public Printer createPrinter(@RequestBody PrinterRequest request) throws UserNotFoundException {

        return authenticationService.createPrinter(request);
    }
}
