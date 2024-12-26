package com.hsl.prompt_be.entities.responses;

import com.hsl.prompt_be.entities.models.OrderDocument;
import com.hsl.prompt_be.entities.models.OrderStatus;
import com.hsl.prompt_be.entities.models.PaymentType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {

    private UUID orderId;

    private String description;
    private int charge;

    private PaymentType paymentType; // on_delivery, online
    private OrderStatus status; // cancelled, complete, invalid, pending

    private boolean paid;
    private boolean completed;

    private Instant createdAt;
    private Instant timeExpected;
    private Instant updatedAt;

    private UUID customerId;
    private UUID printerId;
    private String customerName;
    private String printerName;

    private List<OrderDocument> documents;
}
