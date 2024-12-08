package com.hsl.prompt_be.entities.responses;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserResponse {

    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isPrinter;
    private boolean isEmailVerified;
    private boolean isEnabled;
}
