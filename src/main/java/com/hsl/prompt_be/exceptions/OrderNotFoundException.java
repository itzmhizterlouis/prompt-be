package com.hsl.prompt_be.exceptions;

public class OrderNotFoundException extends EntityNotFoundException {

    public OrderNotFoundException () {

        super("ORDER");
    }
}
