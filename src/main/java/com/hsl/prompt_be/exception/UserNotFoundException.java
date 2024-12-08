package com.hsl.prompt_be.exception;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException() {

        super("USER");
    }
}
