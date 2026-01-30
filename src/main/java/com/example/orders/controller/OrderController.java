package com.example.orders.controller;

// Implement REST controller for Orders API.
//
// Base path: /orders
//
// Endpoints:
//
// 1) POST /orders
//    - Request body: CreateOrderRequest
//    - Validate request using @Valid
//    - Delegate creation logic to OrderService.createOrder()
//    - Return HTTP 201 Created with OrderResponse
//
// 2) GET /orders
//    - Pagination query parameters:
//        page (int, default = 1, MUST be >= 1, API uses 1-based pagination)
//        limit (int, default = 10, MUST be between 1 and 100)
//    - Filtering query parameters (all optional):
//        status (OrderStatus)
//        minAmount (BigDecimal)
//        maxAmount (BigDecimal)
//        dateFrom (LocalDate, ISO format yyyy-MM-dd)
//        dateTo (LocalDate, ISO format yyyy-MM-dd)
//
//    - Delegate all business logic, validation, pagination and filtering to OrderService.getOrders()
//    - Return HTTP 200 OK with PagedResponse<OrderResponse>
//
// Requirements:
// - Use @RestController and @RequestMapping("/orders").
// - Use constructor injection for OrderService.
// - Use @Validated at controller level.
// - Use @RequestParam for query parameters with default values.
// - Use @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) for LocalDate parameters.
// - Do NOT implement validation or business logic in the controller.
// - Rely on GlobalExceptionHandler for error handling (400 / 500 responses).
//
// Imports to use:
// - com.example.orders.service.OrderService
// - com.example.orders.dto.CreateOrderRequest
// - com.example.orders.dto.OrderResponse
// - com.example.orders.dto.PagedResponse
// - com.example.orders.model.OrderStatus
//
// The controller should remain thin and readable.

import com.example.orders.exception.BadRequestException;
import com.example.orders.service.OrderService;
import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.dto.PagedResponse;
import com.example.orders.model.OrderStatus;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.orders.dto.UpdateOrderRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<OrderResponse>> getOrders(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {

        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new BadRequestException("dateFrom must be <= dateTo");
        }

        PagedResponse<OrderResponse> response = orderService.getOrders(
                page, limit, status, minAmount, maxAmount, dateFrom, dateTo);
        return ResponseEntity.ok(response);
}

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @PutMapping("/{id}")
    public OrderResponse update(@PathVariable Long id, @Valid @RequestBody UpdateOrderRequest request) {
        return orderService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }

}


