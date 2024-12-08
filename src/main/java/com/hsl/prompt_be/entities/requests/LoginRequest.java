package com.hsl.prompt_be.entities.requests;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
}
