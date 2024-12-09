package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.Printer;
import com.hsl.prompt_be.entities.requests.PrinterRequest;
import com.hsl.prompt_be.exceptions.PrinterNotFoundException;
import com.hsl.prompt_be.repositories.PrinterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PrinterService {

    private final PrinterRepository printerRepository;

    public List<Printer> getAllPrinters() {

        return printerRepository.findAll();
    }

    public Printer getPrinterById(UUID printerId) throws PrinterNotFoundException {

        return printerRepository.findById(printerId).orElseThrow(PrinterNotFoundException::new);
    }

    public List<Printer> searchPrinterByTag(String tag) {

        return printerRepository.findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(tag, tag);
    }

    public Printer updatePrinter(UUID printerId, PrinterRequest request) throws PrinterNotFoundException {

        Printer printer = printerRepository.findById(printerId).orElseThrow(PrinterNotFoundException::new);

        printer.setName(request.getName());
        printer.setLocation(request.getLocation().toUpperCase());
        printer.setDescription(request.getDescription());
        printer.setColouredRate(request.getColouredRate());
        printer.setUncolouredRate(request.getUncolouredRate());
        printer.setBankName(request.getBankName());
        printer.setAccountName(request.getAccountName());
        printer.setAccountNumber(request.getAccountNumber());
        printer.setWeekdayClosing(request.getWeekdayClosing());
        printer.setWeekdayOpening(request.getWeekdayOpening());
        printer.setWeekendClosing(request.getWeekendClosing());
        printer.setWeekendOpening(request.getWeekendOpening());
        printer.setUpdatedAt(Instant.now());

        return printerRepository.save(printer);
    }
}
