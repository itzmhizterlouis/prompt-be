package com.hsl.prompt_be.entities.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KorapayCheckoutResponse {

    private String status;
    private String message;
    private KorapayCheckoutData data;
}
