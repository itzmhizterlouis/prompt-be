package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.PrinterWallet;
import com.hsl.prompt_be.repositories.PrinterWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PrinterWalletService {

    private final PrinterWalletRepository printerWalletRepository;

    public PrinterWallet save(PrinterWallet printerWallet) {

        return printerWalletRepository.save(printerWallet);
    }
}
