package com.hsl.prompt_be.controllers;

import com.hsl.prompt_be.entities.models.Printer;
import com.hsl.prompt_be.entities.requests.PrinterRequest;
import com.hsl.prompt_be.exceptions.PrinterNotFoundException;
import com.hsl.prompt_be.services.PrinterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("printers")
@RestController
public class PrinterController {

    private final PrinterService printerService;

    @GetMapping
    public List<Printer> getAllPrinters() {

        return printerService.getAllPrinters();
    }

    @GetMapping("{printerId}")
    public Printer getPrinterById(@PathVariable UUID printerId) throws PrinterNotFoundException {

        return printerService.getPrinterById(printerId);
    }

    @GetMapping("search/{tag}")
    public List<Printer> searchPrinterByTag(@PathVariable String tag) {

        return printerService.searchPrinterByTag(tag);
    }

    @PutMapping("{printerId}")
    public Printer updatePrinter(@PathVariable UUID printerId, @RequestBody PrinterRequest request) throws PrinterNotFoundException {

        return printerService.updatePrinter(printerId, request);
    }
}
