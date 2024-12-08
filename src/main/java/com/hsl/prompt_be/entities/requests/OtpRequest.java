package com.hsl.prompt_be.entities.requests;

import lombok.Data;

import java.util.UUID;

@Data
public class OtpRequest {

    private int otp;
    private UUID userId;
}
