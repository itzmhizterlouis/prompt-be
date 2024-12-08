package com.hsl.prompt_be.exception;

public class EntityNotFoundException extends PrinthubException {

    public EntityNotFoundException (String ENTITY) {

        super(ENTITY + " not found");
    }
}
