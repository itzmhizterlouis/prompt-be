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
@Data
@Builder
@Entity
@Table(name = "printer_wallet")
public class PrinterWallet {

    @Id
    @Builder.Default UUID printerWalletId = UUID.randomUUID();

    @Builder.Default private int balance = 0;

    private UUID printerId;

    @Builder.Default private Instant createdAt = Instant.now();
    @Builder.Default private Instant updatedAt = Instant.now();
}
