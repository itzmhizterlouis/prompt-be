package com.hsl.prompt_be.entities.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KorapayPayoutResponse {

    private boolean status;
    private String message;
    private KorapayPayoutData data;
}
