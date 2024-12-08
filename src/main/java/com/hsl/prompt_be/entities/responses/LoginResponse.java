package com.hsl.prompt_be.entities.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String token;
    private boolean isPrinter;
    private boolean isEmailVerified;
    private boolean isEnabled;
}
