package com.hsl.prompt_be.entities.requests;

import com.hsl.prompt_be.entities.models.CustomerDetails;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KorapayPayoutDestination {

    private String type;
    private String amount;
    private String currency;
    private String narration;
    private KorapayBank bank_account;
    private CustomerDetails customer;
}
