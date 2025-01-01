package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.Printer;
import com.hsl.prompt_be.entities.requests.PrinterRequest;
import com.hsl.prompt_be.exceptions.PrinterNotFoundException;
import com.hsl.prompt_be.exceptions.PrinthubException;
import com.hsl.prompt_be.repositories.PrinterRepository;
import com.hsl.prompt_be.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PrinterService {

    private final PrinterRepository printerRepository;

    public List<Printer> getAllPrinters() {

        return printerRepository.findAll().parallelStream().map(Printer::toDto).collect(Collectors.toList());
    }

    public Printer getPrinterById(UUID printerId) throws PrinterNotFoundException {

        return printerRepository.findById(printerId).orElseThrow(PrinterNotFoundException::new).toDto();
    }

    public List<Printer> searchPrinterByNameOrLocation(String tag) {

        return printerRepository.findAllByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(tag, tag)
                .parallelStream().map(Printer::toDto).collect(Collectors.toList());
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

        return printerRepository.save(printer).toDto();
    }

    public Optional<Printer> getLoggedInPrinter() throws PrinthubException {

        return getPrinterByUserId(UserUtil.getLoggedInUser().getUserId());
    }

    public Optional<Printer> getPrinterByUserId(UUID userId) {

        return printerRepository.findByUserId(userId);
    }
}
