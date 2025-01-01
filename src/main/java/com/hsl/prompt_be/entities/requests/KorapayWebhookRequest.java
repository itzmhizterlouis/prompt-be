package com.hsl.prompt_be.entities.requests;

import lombok.Data;

@Data
public class KorapayWebhookRequest {

    private String event;
    private KorapayWebhookData data;
}
