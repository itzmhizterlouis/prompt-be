package com.hsl.prompt_be.entities.responses;

import com.hsl.prompt_be.entities.models.OpenStatus;
import com.hsl.prompt_be.entities.models.Order;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.time.Instant;

@Data
@Builder
public class VendorDashboardResponse {

    private int numberOfTodayOrders;
    private int balance;
    private int review;
    private OpenStatus open;
    private Instant weekdayClosing;
    private Instant weekdayOpening;
    private Instant weekendClosing;
    private Instant weekendOpening;
//    private Page<Order> orders;
}
