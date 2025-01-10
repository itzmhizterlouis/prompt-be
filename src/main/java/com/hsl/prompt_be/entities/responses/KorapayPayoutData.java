package com.hsl.prompt_be.entities.responses;

import com.hsl.prompt_be.entities.models.CustomerDetails;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KorapayPayoutData {

    private String amount;
    private String fee;
    private String currency;
    private String status;
    private String reference;
    private String narration;
    private CustomerDetails customer;
}
