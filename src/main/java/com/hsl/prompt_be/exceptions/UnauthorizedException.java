package com.hsl.prompt_be.exceptions;

public class UnauthorizedException extends PrinthubException {

    public UnauthorizedException (String message) {

        super("Unable to perform this action due to " + message);
    }
}
