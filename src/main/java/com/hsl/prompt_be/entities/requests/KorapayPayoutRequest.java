package com.hsl.prompt_be.entities.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KorapayPayoutRequest {

    private String reference;
    private KorapayPayoutDestination destination;
}
