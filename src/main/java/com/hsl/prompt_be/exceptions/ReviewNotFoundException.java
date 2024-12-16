package com.hsl.prompt_be.exceptions;

public class ReviewNotFoundException extends EntityNotFoundException {

    public ReviewNotFoundException() {

        super("REVIEW");
    }
}
