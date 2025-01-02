package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.Order;
import com.hsl.prompt_be.entities.models.OrderDocument;
import com.hsl.prompt_be.entities.models.Payment;
import com.hsl.prompt_be.entities.models.PaymentStatus;
import com.hsl.prompt_be.entities.models.Printer;
import com.hsl.prompt_be.entities.models.User;
import com.hsl.prompt_be.entities.requests.EmailDetails;
import com.hsl.prompt_be.entities.requests.KorapayWebhookRequest;
import com.hsl.prompt_be.entities.requests.OrderRequest;
import com.hsl.prompt_be.entities.requests.SearchOrderRequest;
import com.hsl.prompt_be.entities.requests.UpdateOrderRequest;
import com.hsl.prompt_be.entities.responses.AppResponse;
import com.hsl.prompt_be.entities.responses.KorapayCheckoutResponse;
import com.hsl.prompt_be.entities.responses.OrderResponse;
import com.hsl.prompt_be.exceptions.OrderNotFoundException;
import com.hsl.prompt_be.exceptions.PrinterNotFoundException;
import com.hsl.prompt_be.exceptions.PrinterWalletNotFoundException;
import com.hsl.prompt_be.exceptions.PrinthubException;
import com.hsl.prompt_be.exceptions.UnauthorizedException;
import com.hsl.prompt_be.exceptions.UserNotFoundException;
import com.hsl.prompt_be.repositories.OrderRepository;
import com.hsl.prompt_be.repositories.specifications.OrderSpecification;
import com.hsl.prompt_be.utils.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PrinterService printerService;
    private final PaymentService paymentService;
    private final PrinterWalletService printerWalletService;
    private final UserService userService;
    private final EmailService emailService;

    public OrderResponse createOrder(UUID printerId, OrderRequest request) throws PrinthubException {

        Order order = Order.builder()
                .description(request.getDescription())
                .charge(request.getCharge())
                .paymentType(request.getPaymentType())
                .timeExpected(request.getTimeExpected())
                .customerId(UserUtil.getLoggedInUser().getUserId())
                .printerId(printerId)
                .build();

        List<OrderDocument> orderDocuments = new ArrayList<>();
        request.getDocuments().forEach(document ->
                orderDocuments.add(
                    OrderDocument.builder()
                            .name(document.getName())
                            .uri(document.getUri())
                            .copies(document.getCopies())
                            .pages(document.getPages())
                            .coloured(document.isColoured())
                            .order(order)
                            .build()));

        order.setDocuments(orderDocuments);
        OrderResponse orderResponse = convertOrderToDto(orderRepository.save(order));

        sendOrderInformationToEmail(
                orderResponse.getCustomerId(), orderResponse.getPrinterId(), "Print Order Notification",
                orderResponse.getCustomerName() + " placed an order to be received at "
                        + convertInstantToHumanReadable(orderResponse.getTimeExpected()),
                "You placed an order to " + orderResponse.getPrinterName() + " to be received at "
                        + convertInstantToHumanReadable(orderResponse.getTimeExpected())
        );

        return orderResponse;
    }

    public List<OrderResponse> searchOrders(SearchOrderRequest request) {

        Specification<Order> spec = OrderSpecification.byDynamicCriteria(request.getSpecifications());
        return orderRepository.findAll(spec).parallelStream().map(order -> {
            try {
                return convertOrderToDto(order);
            } catch (PrinterNotFoundException | UserNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    public OrderResponse updateOrder(UUID orderId, UpdateOrderRequest request) throws PrinthubException {

        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        throwErrorIfUserNotInvolvedWithOrder(order);

        order.setStatus(request.getStatus());

        order.setUpdatedAt(Instant.now());
        OrderResponse response = convertOrderToDto(orderRepository.save(order));

        sendOrderInformationToEmail(
                response.getCustomerId(), response.getPrinterId(), "Printhub Order Update",
                null,
                "Your order status is now " + response.getStatus()
        );

        return response;
    }

    public OrderResponse getOrderById(UUID orderId) throws OrderNotFoundException, UserNotFoundException, PrinterNotFoundException {

        return convertOrderToDto(orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new));
    }

    public KorapayCheckoutResponse onlineOrderPayment(UUID orderId) throws OrderNotFoundException, UserNotFoundException {

        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        User user = userService.findByUserId(order.getCustomerId());

        return paymentService.initiateOrderPayment(order, user);
    }

    @Transactional
    public AppResponse successfulPayment(KorapayWebhookRequest request) throws OrderNotFoundException, PrinterWalletNotFoundException {

        Payment payment = Payment.builder()
                .orderId(UUID.fromString(request.getData().getReference()))
                .status(request.getData().getStatus())
                .method(request.getData().getPayment_method())
                .amount(request.getData().getAmount()).build();

        Order order = orderRepository.findById(UUID.fromString(request.getData().getReference())).orElseThrow(OrderNotFoundException::new);
        order.setPaid(true);
        order.setUpdatedAt(Instant.now());

        if (request.getData().getStatus().equals(PaymentStatus.success)) {

            printerWalletService.addAmountToPrinterWallet(order.getPrinterId(), order.getCharge());
            orderRepository.save(order);
        }

        paymentService.savePayment(payment);

        return AppResponse.builder()
                .message("Payment reference has been successfully saved")
                .status(HttpStatus.OK).build();
    }

    public void throwErrorIfUserNotInvolvedWithOrder(Order order) throws PrinthubException {

        UUID userId = UserUtil.getLoggedInUser().getUserId();
        if (userId.equals(order.getCustomerId())) {
            return;
        }

        Optional<Printer> printer = printerService.getLoggedInPrinter();
        if (printer.isPresent()) {
            if (printer.get().getPrinterId().equals(order.getPrinterId())) {
                return;
            }
        }

        throw new UnauthorizedException("You're not associated with this order");
    }

    public OrderResponse convertOrderToDto(Order order) throws PrinterNotFoundException, UserNotFoundException {

        User user = userService.findByUserId(order.getCustomerId());
        Printer printer = printerService.getPrinterById(order.getPrinterId());

        return order.toDto(user.getFirstName() + " " + user.getLastName(), printer.getName());
    }

    public void throwErrorIfUserHasNoOrders(UUID printerId) throws PrinthubException {

        if (!orderRepository.existsByPrinterIdAndCustomerId(printerId, UserUtil.getLoggedInUser().getUserId())) {

            throw new UnauthorizedException("You've not made an order to this printer");
        }
    }

    public String convertInstantToHumanReadable(Instant instantTime) {

        return DateTimeFormatter
                .ofPattern("MMMM dd, yyyy, hh:mm:ss a")
                .withZone(ZoneId.of("GMT+1"))
                .format(instantTime);
    }

    private void sendOrderInformationToEmail(UUID customerId, UUID printerId, String subject, String printerMessageBody, String userMessageBody) throws UserNotFoundException, PrinterNotFoundException {

        User user = userService.findByUserId(customerId);
        User printerUser = userService.findByUserId(printerService.getPrinterById(printerId).getUserId());

        if (userMessageBody != null) {
            // Send email to user
            emailService.sendSimpleMail(
                    EmailDetails.builder()
                            .recipient(user.getEmail())
                            .subject(subject)
                            .messageBody(userMessageBody).build()
            );
        }
        if (printerMessageBody != null) {
            // Send email to printer
            emailService.sendSimpleMail(
                    EmailDetails.builder()
                            .recipient(printerUser.getEmail())
                            .subject(subject)
                            .messageBody(printerMessageBody).build()
            );
        }
    }
}
