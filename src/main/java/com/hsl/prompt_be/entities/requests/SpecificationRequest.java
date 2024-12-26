package com.hsl.prompt_be.entities.requests;

import lombok.Data;

@Data
public class SpecificationRequest<T> {

    private String tag;
    private T value;
    private String operator;
}
