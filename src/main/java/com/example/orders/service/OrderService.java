package com.example.orders.service;

// Implement OrderService for Orders API.
//
// Responsibilities:
// 1) createOrder(CreateOrderRequest request) -> OrderResponse
//    - Map request to Order entity.
//    - If request.status is null, default to OrderStatus.NEW.
//    - Save via OrderRepository.
//    - Return OrderResponse (use a mapper or constructor).
//
// 2) getOrders(page, limit, status, minAmount, maxAmount, dateFrom, dateTo) -> PagedResponse<OrderResponse>
//    Pagination rules (IMPORTANT):
//    - API page is 1-based (page >= 1). Convert to Spring Pageable 0-based: PageRequest.of(page - 1, limit, Sort.by("createdAt").descending()).
//    - limit must be between 1 and 100 (inclusive).
//    - If page < 1 or limit out of range, throw BadRequestException with a clear message.
//
//    Filter validation rules:
//    - If minAmount != null and maxAmount != null and minAmount > maxAmount -> throw BadRequestException.
//    - If dateFrom != null and dateTo != null and dateFrom is after dateTo -> throw BadRequestException.
//
//    Query execution:
//    - Build Specification<Order> using OrderSpecifications.build(status, minAmount, maxAmount, dateFrom, dateTo).
//    - Call repository.findAll(spec, pageable).
//    - Map Page<Order> content to List<OrderResponse>.
//    - Return PagedResponse with: items, page (1-based), limit, totalItems, totalPages.
//
// Additional requirements:
// - Use constructor injection for dependencies.
// - Keep business logic in service, controller should be thin.
// - Use java.math.BigDecimal and java.time.LocalDate for filters; Order.createdAt is Instant.
// - Add small helper methods if needed for validation and mapping.
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
