package com.hsl.prompt_be.exception;

public class OtpNotFoundException extends EntityNotFoundException {

    public OtpNotFoundException() {

        super ("OTP");
    }
}