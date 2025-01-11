package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.OpenStatus;
import com.hsl.prompt_be.entities.models.Printer;
import com.hsl.prompt_be.entities.models.UserWallet;
import com.hsl.prompt_be.entities.requests.PrinterRequest;
import com.hsl.prompt_be.entities.responses.AppResponse;
import com.hsl.prompt_be.entities.responses.KorapayPayoutResponse;
import com.hsl.prompt_be.entities.responses.VendorDashboardResponse;
import com.hsl.prompt_be.exceptions.PrinterNotFoundException;
import com.hsl.prompt_be.exceptions.PrinthubException;
import com.hsl.prompt_be.exceptions.UnauthorizedException;
import com.hsl.prompt_be.exceptions.WalletBalanceException;
import com.hsl.prompt_be.repositories.OrderRepository;
import com.hsl.prompt_be.repositories.PrinterRepository;
import com.hsl.prompt_be.utils.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PrinterService {

    private final PrinterRepository printerRepository;
    private final UserWalletService userWalletService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    @Value("${spring.cron.key}")
    private String cron_key;

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

    public AppResponse updatePrinterOpenStatus(OpenStatus open) throws PrinthubException {

        Printer printer = getLoggedInPrinter().get();

        printer.setOpen(open);
        printer.setUpdatedAt(Instant.now());
        printerRepository.save(printer).toDto();

        return AppResponse.builder()
                .message("You are now " + open.toString())
                .status(HttpStatus.OK).build();
    }

    public VendorDashboardResponse getVendorDashboardDetails() throws PrinthubException {

        throwErrorIfLoggedInUserNotPrinter();

        Printer printer = getLoggedInPrinter().orElseThrow(PrinterNotFoundException::new).toDto();
//        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return VendorDashboardResponse.builder()
                .numberOfTodayOrders(getOrdersByPrinterIdToday(printer.getPrinterId()))
                .balance(userWalletService.getLoggedInUserWallet().getBalance())
                .rating(printer.getRating())
                .totalNumberOfReviews(printer.getReviews().size())
                .open(printer.getOpen())
                .weekdayClosing(printer.getWeekdayClosing())
                .weekendClosing(printer.getWeekendClosing())
                .weekdayOpening(printer.getWeekdayOpening())
                .weekendOpening(printer.getWeekendOpening()).build();
//                .orders(orderRepository.findAllByPrinterId(printer.getPrinterId(), pageRequest)).build();
    }

    @Transactional
    public KorapayPayoutResponse withdrawBalance() throws PrinthubException {

        UserWallet userWallet = userWalletService.getLoggedInUserWallet();
        Printer printer = getLoggedInPrinter().orElseThrow(PrinterNotFoundException::new);

        if (userWallet.getBalance() < 1000) {

            throw new WalletBalanceException();
        }

        return paymentService.callKorapayPayoutApi(printer, userWallet);
    }

    @Transactional
    public AppResponse openStatusCron(String key) throws UnauthorizedException {

        if (!cron_key.equals(key)) {
            throw new UnauthorizedException("You're not allowed to access this url");
        }

        List<Printer> printers = printerRepository.findAll();
        for (Printer printer : printers) {
            printer.setOpen(OpenStatus.NIL);
        }
        printerRepository.saveAll(printers);

        return AppResponse.builder()
                .message("Statuses have been successfully updated")
                .status(HttpStatus.OK).build();
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

    public int getOrdersByPrinterIdToday(UUID printerId) {
        Instant startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusMillis(1);

        return orderRepository.countByPrinterIdAndCreatedAtBetween(printerId, startOfDay, endOfDay);
    }
}
