package com.hsl.prompt_be.controllers;

import com.hsl.prompt_be.entities.models.Printer;
import com.hsl.prompt_be.entities.requests.PrinterRequest;
import com.hsl.prompt_be.entities.requests.UpdateOpenStatusRequest;
import com.hsl.prompt_be.entities.responses.AppResponse;
import com.hsl.prompt_be.entities.responses.KorapayPayoutResponse;
import com.hsl.prompt_be.entities.responses.VendorDashboardResponse;
import com.hsl.prompt_be.exceptions.PrinterNotFoundException;
import com.hsl.prompt_be.exceptions.PrinthubException;
import com.hsl.prompt_be.exceptions.UnauthorizedException;
import com.hsl.prompt_be.services.PrinterService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("printers")
@RestController
public class PrinterController {

    private final PrinterService printerService;

    @Operation(summary = "get all printers endpoint")
    @GetMapping
    public List<Printer> getAllPrinters() {

        return printerService.getAllPrinters();
    }

    @Operation(summary = "get printer by id endpoint")
    @GetMapping("{printerId}")
    public Printer getPrinterById(@PathVariable UUID printerId) throws PrinterNotFoundException {

        return printerService.getPrinterById(printerId);
    }

    @Operation(summary = "search printer by name or location")
    @GetMapping("search/{tag}")
    public List<Printer> searchPrinterByNameOrLocation(@PathVariable String tag) {

        return printerService.searchPrinterByNameOrLocation(tag);
    }

    @Operation(summary = "update printer endpoint")
    @PutMapping("{printerId}")
    public Printer updatePrinter(@PathVariable UUID printerId, @RequestBody PrinterRequest request) throws PrinthubException {

        return printerService.updatePrinter(printerId, request);
    }

    @Operation(summary = "Seperate endpoint for updating open status")
    @PutMapping("{printerId}/update/open_status")
    public Printer updatePrinterOpenStatus(@PathVariable UUID printerId, @RequestBody UpdateOpenStatusRequest request) throws PrinthubException {

        return printerService.updatePrinterOpenStatus(printerId, request.getOpen());
    }

    @Hidden
    @GetMapping("cron/open_status/{cron_key}")
    public AppResponse openStatusCron(@PathVariable String cron_key) throws UnauthorizedException {

        return printerService.openStatusCron(cron_key);
    }

    @Operation(summary = "Vendor Dashboard details", description = "example request: /printers/dashboard?page=0&size=5")
    @GetMapping("dashboard")
    public VendorDashboardResponse getVendorDashboardDetails(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size
    ) throws PrinthubException {

        return printerService.getVendorDashboardDetails();
//        return printerService.getVendorDashboardDetails(page, size);
    }

    @Operation(summary = "Withdraw balance")
    @PostMapping("withdraw")
    public KorapayPayoutResponse withdrawBalance() throws PrinthubException {

        return printerService.withdrawBalance();
    }
}
