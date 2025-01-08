package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.OpenStatus;
import com.hsl.prompt_be.entities.models.Printer;
import com.hsl.prompt_be.entities.models.User;
import com.hsl.prompt_be.entities.requests.PrinterRequest;
import com.hsl.prompt_be.entities.responses.VendorDashboardResponse;
import com.hsl.prompt_be.exceptions.PrinterNotFoundException;
import com.hsl.prompt_be.exceptions.PrinthubException;
import com.hsl.prompt_be.exceptions.UnauthorizedException;
import com.hsl.prompt_be.repositories.OrderRepository;
import com.hsl.prompt_be.repositories.PrinterRepository;
import com.hsl.prompt_be.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final UserWalletService userWalletService;
    private final OrderRepository orderRepository;

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

    public Printer updatePrinter(UUID printerId, PrinterRequest request) throws PrinthubException {

        throwErrorIfLoggedInUserIsNotPrinterId(printerId);

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

    public Printer updatePrinterOpenStatus(UUID printerId, OpenStatus open) throws PrinthubException {

        throwErrorIfLoggedInUserIsNotPrinterId(printerId);

        Printer printer = printerRepository.findById(printerId).orElseThrow(PrinterNotFoundException::new);
        printer.setOpen(open);
        printer.setUpdatedAt(Instant.now());
        return printerRepository.save(printer).toDto();
    }

    public VendorDashboardResponse getVendorDashboardDetails(int page, int size) throws PrinthubException {

        throwErrorIfLoggedInUserNotPrinter();

        User user = UserUtil.getLoggedInUser();
        Printer printer = getPrinterByUserId(user.getUserId()).orElseThrow(PrinterNotFoundException::new).toDto();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return VendorDashboardResponse.builder()
                .numberOfTodayOrders(orderRepository.countByCreatedAt(Instant.now()))
                .balance(userWalletService.getLoggedInUserWallet().getBalance())
                .review(printer.getRating())
                .open(printer.getOpen())
                .weekdayClosing(printer.getWeekdayClosing())
                .weekendClosing(printer.getWeekendClosing())
                .weekdayOpening(printer.getWeekdayOpening())
                .weekendOpening(printer.getWeekendOpening())
                .orders(orderRepository.findAllByPrinterId(printer.getPrinterId(), pageRequest)).build();
    }

    public Optional<Printer> getLoggedInPrinter() throws PrinthubException {

        return getPrinterByUserId(UserUtil.getLoggedInUser().getUserId());
    }

    public Optional<Printer> getPrinterByUserId(UUID userId) {

        return printerRepository.findByUserId(userId);
    }

    public void throwErrorIfLoggedInUserIsNotPrinterId(UUID printerId) throws PrinthubException {

        Printer printer = getPrinterByUserId(UserUtil.getLoggedInUser().getUserId()).get();

        if (!printer.getPrinterId().equals(printerId)) {
            throw new UnauthorizedException("You're not the owner of this account");
        }
    }

    public void throwErrorIfLoggedInUserNotPrinter() throws PrinthubException {

        if (!UserUtil.getLoggedInUser().isPrinter()) {
            throw new UnauthorizedException("You're not a printer");
        }
    }
}
