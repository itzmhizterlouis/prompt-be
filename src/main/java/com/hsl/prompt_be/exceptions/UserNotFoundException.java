package com.hsl.prompt_be.exceptions;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException() {

        super("USER");
    }
}
