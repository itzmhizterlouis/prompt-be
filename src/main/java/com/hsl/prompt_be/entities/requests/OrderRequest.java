package com.hsl.prompt_be.entities.requests;

import com.hsl.prompt_be.entities.models.PaymentType;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class OrderRequest {

    private String description;
    private int charge;
    private PaymentType paymentType;
    private Instant timeExpected;
    private List<OrderDocumentRequest> documents;
}
