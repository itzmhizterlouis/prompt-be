package com.hsl.prompt_be.exceptions;

public class PaymentNotFoundException extends EntityNotFoundException {

    public PaymentNotFoundException () {

        super("PAYMENT");
    }
}
