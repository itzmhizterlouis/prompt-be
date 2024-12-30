package com.hsl.prompt_be.entities.requests;

import com.hsl.prompt_be.entities.models.OrderStatus;
import lombok.Data;

@Data
public class UpdateOrderRequest {

    private OrderStatus status;
}
