package com.hsl.prompt_be.configurations;

import com.hsl.prompt_be.entities.responses.AppResponse;
import com.hsl.prompt_be.exceptions.UnauthorizedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class PrinthubControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppResponse> handleGenericException(Exception ex, WebRequest request) {

        System.out.println(ex.getMessage());
        AppResponse message = AppResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<AppResponse> handleGenericException(DataIntegrityViolationException ex, WebRequest request) {

        System.out.println(ex.getMessage());
        AppResponse message = AppResponse.builder()
                .message("DATA INTEGRITY VIOLATION. " + ex.getMessage())
                .status(HttpStatus.BAD_REQUEST).build();

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<AppResponse> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {

        System.out.println(ex.getMessage());
        AppResponse message = AppResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.UNAUTHORIZED).build();

        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }
}
