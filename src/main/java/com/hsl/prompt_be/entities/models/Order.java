package com.hsl.prompt_be.entities.models;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "orders")
public class Order {

    @Id
    @Builder.Default private UUID orderId = UUID.randomUUID();

    private String description;
    private int charge;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType; // on_delivery, online
    @Enumerated(EnumType.STRING)
    @Builder.Default private OrderStatus status = OrderStatus.PENDING; // cancelled, complete, invalid, pending

    @Builder.Default private boolean paid = false;
    @Builder.Default private boolean completed = false;

    @Builder.Default private Instant createdAt = Instant.now();
    @Builder.Default private Instant timeExpected = Instant.now().plusSeconds(3600);
    @Builder.Default private Instant updatedAt = Instant.now();

    private UUID customerId;
    private UUID printerId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrderDocument> documents;

    public Order toDto(){

        documents.parallelStream().forEach(OrderDocument::toListResponse);
        return this;
    }
}
