package com.hsl.prompt_be.entities.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "printers")
public class Printer {

    @Id
    @Builder.Default private UUID printerId = UUID.randomUUID();

    private String name;
    private String location;
    private String description;
    private int colouredRate;
    private int uncolouredRate;
    private String bankName;
    private String accountName;
    private int accountNumber;

    private UUID userId;

    private Instant weekdayClosing;
    private Instant weekdayOpening;
    private Instant weekendClosing;
    private Instant weekendOpening;

    @Builder.Default private Instant createdAt = Instant.now();
    @Builder.Default private Instant updatedAt = Instant.now();
}
