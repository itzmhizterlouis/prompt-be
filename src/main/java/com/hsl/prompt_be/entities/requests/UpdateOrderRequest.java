package com.hsl.prompt_be.entities.requests;

import com.hsl.prompt_be.entities.models.OrderStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class UpdateOrderRequest {

    private Instant timeExpected;
    private OrderStatus status;
    private boolean paid;
    private boolean completed;
}
