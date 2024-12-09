package com.hsl.prompt_be.exceptions;

public class OtpNotFoundException extends EntityNotFoundException {

    public OtpNotFoundException() {

        super ("OTP");
    }
}