package com.hsl.prompt_be.exceptions;

public class EntityNotFoundException extends PrinthubException {

    public EntityNotFoundException (String ENTITY) {

        super(ENTITY + " not found");
    }
}
