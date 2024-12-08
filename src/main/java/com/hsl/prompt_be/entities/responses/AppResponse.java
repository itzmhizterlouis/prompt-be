package com.hsl.prompt_be.entities.responses;


import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class AppResponse {

    private String message;
    private HttpStatus status;
}
