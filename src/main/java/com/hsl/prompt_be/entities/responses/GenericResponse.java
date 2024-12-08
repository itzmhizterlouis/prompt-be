package com.hsl.prompt_be.entities.responses;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenericResponse {

    private String message;
}
