package com.hsl.prompt_be.entities.requests;

import lombok.Data;

import java.time.Instant;

@Data
public class PrinterRequest {

    private String name;
    private String location;
    private String description;
    private int colouredRate;
    private int uncolouredRate;
    private String bankName;
    private String accountName;
    private long accountNumber;

    private Instant weekdayClosing;
    private Instant weekdayOpening;
    private Instant weekendClosing;
    private Instant weekendOpening;
}
