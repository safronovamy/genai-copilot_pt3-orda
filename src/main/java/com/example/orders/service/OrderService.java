package com.example.orders.service;

import com.example.orders.model.Order;
import com.example.orders.model.OrderStatus;
import com.example.orders.repository.OrderRepository;
import com.example.orders.spec.OrderSpecifications;
import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.dto.PagedResponse;
import com.example.orders.exception.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.example.orders.dto.UpdateOrderRequest;
import com.example.orders.exception.NotFoundException;


@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = Order.builder()
                .customerName(request.getCustomerName())
                .status(request.getStatus() != null ? request.getStatus() : OrderStatus.NEW)
                .amount(request.getAmount())
                .build();

        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }
    public PagedResponse<OrderResponse> getOrders(
            int page,
            int limit,
            OrderStatus status,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            LocalDate dateFrom,
            LocalDate dateTo) {

        validatePaginationParams(page, limit);
        validateFilterParams(minAmount, maxAmount, dateFrom, dateTo);

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        var spec = OrderSpecifications.build(status, minAmount, maxAmount, dateFrom, dateTo);
        Page<Order> orderPage = orderRepository.findAll(spec, pageable);

        var orderResponses = orderPage.getContent().stream()
                .map(this::mapToOrderResponse)
                .toList();

        return new PagedResponse<>(
                orderResponses,
                page,
                limit,
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );
    }
    private void validatePaginationParams(int page, int limit) {
        if (page < 1) {
            throw new BadRequestException("Page number must be at least 1.");
        }
        if (limit < 1 || limit > 100) {
            throw new BadRequestException("Limit must be between 1 and 100.");
        }
    }
    private void validateFilterParams(
            java.math.BigDecimal minAmount,
            java.math.BigDecimal maxAmount,
            java.time.LocalDate dateFrom,
            java.time.LocalDate dateTo) {
        if (minAmount != null && maxAmount != null && minAmount.compareTo(maxAmount) > 0) {
            throw new BadRequestException("minAmount cannot be greater than maxAmount.");
        }
        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new BadRequestException("dateFrom cannot be after dateTo.");
        }
    }
    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getStatus(),
                order.getAmount(),
                order.getCreatedAt()
        );
    }

    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));
        return mapToOrderResponse(order);
    }

    public OrderResponse update(Long id, UpdateOrderRequest req) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));

        if (req.getCustomerName() != null) {
            order.setCustomerName(req.getCustomerName());
        }
        if (req.getAmount() != null) {
            order.setAmount(req.getAmount());
        }
        if (req.getStatus() != null) {
            order.setStatus(req.getStatus());
        }

        Order saved = orderRepository.save(order);
        return mapToOrderResponse(saved);
    }

    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException("Order not found: " + id);
        }
        orderRepository.deleteById(id);
    }


}
