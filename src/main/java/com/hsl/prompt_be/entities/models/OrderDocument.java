package com.hsl.prompt_be.entities.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_documents")
public class OrderDocument {

    @Id
    @Builder.Default private UUID orderDocumentId = UUID.randomUUID();

    private String name;
    private String uri;

    @Builder.Default private int copies = 1;
    @Builder.Default private int pages = 1;
    @Builder.Default private boolean coloured = false;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public OrderDocument toListResponse() {

        order = null;
        return this;
    }
}
