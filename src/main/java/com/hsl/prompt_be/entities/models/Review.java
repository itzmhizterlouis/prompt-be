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

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @Builder.Default
    private UUID reviewId = UUID.randomUUID();

    private int rating;
    private String comment;

    @Builder.Default private Instant createdAt = Instant.now();
    @Builder.Default private Instant updatedAt = Instant.now();

    private UUID ownerId;
    private UUID printerId;
}
