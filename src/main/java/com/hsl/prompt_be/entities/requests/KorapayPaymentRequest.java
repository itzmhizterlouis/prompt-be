package com.hsl.prompt_be.entities.requests;

import com.hsl.prompt_be.entities.models.CustomerDetails;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KorapayPaymentRequest {

    private int amount;
    private String redirect_url;
    private String currency;
    private String reference;
    private String narration;
    private String notification_url;
    private CustomerDetails customer;
}
