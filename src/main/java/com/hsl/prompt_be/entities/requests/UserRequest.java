package com.hsl.prompt_be.entities.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @Builder.Default
    private boolean emailVerified = false;
}
