package com.hsl.prompt_be.entities.requests;

import com.hsl.prompt_be.entities.models.PaymentMethod;
import com.hsl.prompt_be.entities.models.PaymentStatus;
import lombok.Data;

@Data
public class KorapayWebhookData {

    private String reference;
    private int amount;
    private PaymentStatus status;
    private PaymentMethod payment_method;
}
