package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.PrinterWallet;
import com.hsl.prompt_be.exceptions.PrinterWalletNotFoundException;
import com.hsl.prompt_be.repositories.PrinterWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PrinterWalletService {

    private final PrinterWalletRepository printerWalletRepository;

    public PrinterWallet save(PrinterWallet printerWallet) {

        return printerWalletRepository.save(printerWallet);
    }

    public PrinterWallet addAmountToPrinterWallet(UUID printerId, int amount) throws PrinterWalletNotFoundException {

        PrinterWallet printerWallet = printerWalletRepository.findByPrinterId(printerId).orElseThrow(PrinterWalletNotFoundException::new);
        printerWallet.setBalance(printerWallet.getBalance() + amount);
        printerWallet.setUpdatedAt(Instant.now());

        return printerWalletRepository.save(printerWallet);
    }
}
