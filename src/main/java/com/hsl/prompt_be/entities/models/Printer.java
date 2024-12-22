package com.hsl.prompt_be.entities.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
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

    @Transient
    private int rating;

    @OneToMany(mappedBy = "printerId", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Review> reviews;

    @Builder.Default private Instant createdAt = Instant.now();
    @Builder.Default private Instant updatedAt = Instant.now();

    public Printer toDto() {

        rating = reviews.stream().mapToInt(Review::getRating).sum();
        return this;
    }
}
