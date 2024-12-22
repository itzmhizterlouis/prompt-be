package com.hsl.prompt_be.repositories;

import com.hsl.prompt_be.entities.models.PrinterWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PrinterWalletRepository extends JpaRepository<PrinterWallet, UUID> {
}
