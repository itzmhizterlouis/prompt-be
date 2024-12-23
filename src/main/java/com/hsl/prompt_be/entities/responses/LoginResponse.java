package com.hsl.prompt_be.entities.responses;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LoginResponse {

    private String token;
    private UUID userId;
    private boolean isPrinter;
    private boolean isEmailVerified;
    private boolean isEnabled;
    @Builder.Default private UUID printerId = null;
}
