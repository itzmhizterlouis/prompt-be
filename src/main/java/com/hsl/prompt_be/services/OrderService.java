package com.hsl.prompt_be.services;

import com.hsl.prompt_be.entities.models.Order;
import com.hsl.prompt_be.entities.models.OrderDocument;
import com.hsl.prompt_be.entities.models.Printer;
import com.hsl.prompt_be.entities.requests.OrderRequest;
import com.hsl.prompt_be.entities.requests.SearchOrderRequest;
import com.hsl.prompt_be.entities.requests.UpdateOrderRequest;
import com.hsl.prompt_be.exceptions.OrderNotFoundException;
import com.hsl.prompt_be.exceptions.PrinthubException;
import com.hsl.prompt_be.exceptions.UnauthorizedException;
import com.hsl.prompt_be.repositories.OrderRepository;
import com.hsl.prompt_be.repositories.specifications.OrderSpecification;
import com.hsl.prompt_be.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    public Order createOrder(UUID printerId, OrderRequest request) throws PrinthubException {

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

        return orderRepository.save(order).toDto();
    }

    public List<Order> searchOrders(SearchOrderRequest request) {

        Specification<Order> spec = OrderSpecification.byDynamicCriteria(request.getSpecifications());
        return orderRepository.findAll(spec).parallelStream().map(Order::toDto).collect(Collectors.toList());
    }

    public Order updateOrder(UUID orderId, UpdateOrderRequest request) throws PrinthubException {

        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        throwErrorIfUserNotInvolvedWithOrder(order);

        order.setStatus(request.getStatus());
        order.setTimeExpected(request.getTimeExpected());
        order.setPaid(request.isPaid());
        order.setCompleted(request.isCompleted());

        order.setUpdatedAt(Instant.now());
        return orderRepository.save(order).toDto();
    }

    public Order getOrderById(UUID orderId) throws OrderNotFoundException {

        return orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new).toDto();
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

    public void throwErrorIfUserHasNoOrders(UUID printerId) throws PrinthubException {

        if (!orderRepository.existsByPrinterIdAndCustomerId(printerId, UserUtil.getLoggedInUser().getUserId())) {

            throw new UnauthorizedException("You've not made an order to this printer");
        }
    }
}
