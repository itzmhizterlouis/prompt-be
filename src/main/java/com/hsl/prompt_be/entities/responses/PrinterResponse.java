package com.hsl.prompt_be.entities.responses;

import com.hsl.prompt_be.entities.models.Review;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PrinterResponse {

    private UUID printerId;

    private String name;
    private String location;
    private String description;
    private int colouredRate;
    private int uncolouredRate;
    private String bankName;
    private String accountName;
    private long accountNumber;

    private UUID userId;

    private Instant weekdayClosing;
    private Instant weekdayOpening;
    private Instant weekendClosing;
    private Instant weekendOpening;

    private int rating;
    private List<Review> reviews;

    private Instant createdAt;
    private Instant updatedAt;
}
