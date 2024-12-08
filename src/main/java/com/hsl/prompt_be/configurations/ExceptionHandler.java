package com.hsl.prompt_be.configurations;

import com.hsl.prompt_be.entities.responses.AppResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {Exception.class})
    public ResponseEntity<AppResponse> resourceNotFoundException(Exception ex, WebRequest request) {

        System.out.println(ex.getMessage());
        AppResponse message = AppResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}
