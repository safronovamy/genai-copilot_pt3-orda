package com.example.orders.controller;

// Create JUnit 5 tests for OrderController using @WebMvcTest(OrderController.class).
//
// Requirements:
// - Use MockMvc to test HTTP requests.
// - Mock OrderService with @MockBean.
// - Use Jackson ObjectMapper for JSON.
// - Cover both endpoints:
//   1) POST /orders
//   2) GET /orders
//
// Test cases (minimum):
// - POST /orders returns 201 and body contains id, customerName, status, amount, createdAt.
// - POST /orders returns 400 for invalid body (blank customerName).
// - POST /orders returns 400 for invalid body (amount is null).
// - POST /orders returns 400 for invalid body (amount <= 0).
//
// - GET /orders returns 200 and expected JSON structure (items/page/limit/totalItems/totalPages).
// - GET /orders returns 400 when page=0 (service throws BadRequestException; verify 400).
// - GET /orders returns 400 when limit=101 (service throws BadRequestException; verify 400).
// - GET /orders with filters passes query params to service:
//   status=PAID, minAmount=10, maxAmount=100, dateFrom=2026-01-01, dateTo=2026-01-10.
//   Verify OrderService.getOrders(...) called with correct arguments.
//
// Notes:
// - Do not test business logic here; only controller wiring and validation.
// - For 400 errors, assert that response JSON contains fields: status and message (ApiError).
// - Use given/when patterns and Mockito verify() calls.

import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.dto.PagedResponse;
import com.example.orders.exception.GlobalExceptionHandler;
import com.example.orders.model.OrderStatus;
import com.example.orders.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateOrder_Success() throws Exception {
        // Arrange: mock orderService.createOrder(...) to return a sample OrderResponse
        // Act: perform POST /orders with valid JSON body
        // Assert: status is 201 and response json has id, customerName, status, amount, createdAt
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerName("John Doe")
                .status(OrderStatus.NEW)
                .amount(new java.math.BigDecimal("99.99"))
                .build();
        OrderResponse response = OrderResponse.builder()
                .id(1L)
                .customerName("John Doe")
                .status(OrderStatus.NEW)
                .amount(new java.math.BigDecimal("99.99"))
                .createdAt(java.time.Instant.parse("2026-01-10T10:00:00Z"))
                .build();
        Mockito.when(orderService.createOrder(org.mockito.ArgumentMatchers.any(CreateOrderRequest.class)))
                .thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.amount").value(99.99))
                .andExpect(jsonPath("$.createdAt").exists());
        Mockito.verify(orderService, Mockito.times(1))
                .createOrder(org.mockito.ArgumentMatchers.any(CreateOrderRequest.class));


    }


    @Test
    void testCreateOrder_InvalidCustomerName() throws Exception {
        // Generate this test:
        // - Create request with blank customerName="   " and valid amount
        // - POST /orders
        // - Expect 400 Bad Request
        // - Assert jsonPath $.status exists (ApiError)
        // - Verify orderService.createOrder(...) is never called
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerName("   ")
                .amount(new java.math.BigDecimal("50.00"))
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists());
        Mockito.verify(orderService, Mockito.never()).createOrder(org.mockito.ArgumentMatchers.any(CreateOrderRequest.class));

    }


    @Test
    void testCreateOrder_NullAmount() throws Exception {
        // Generate this test:
        // - Create request with amount=null and valid customerName
        // - POST /orders
        // - Expect 400
        // - Verify service not called
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerName("Jane Doe")
                .amount(null)
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists());

        Mockito.verify(orderService, Mockito.never()).createOrder(org.mockito.ArgumentMatchers.any(CreateOrderRequest.class));

    }


    @Test
    void testCreateOrder_NonPositiveAmount() throws Exception {
        // Generate this test:
        // - Create request with amount=0 (or -1) and valid customerName
        // - POST /orders
        // - Expect 400
        // - Verify service not called
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerName("Jane Doe")
                .amount(new java.math.BigDecimal("0"))
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists());

        Mockito.verify(orderService, Mockito.never()).createOrder(org.mockito.ArgumentMatchers.any(CreateOrderRequest.class));

    }


    @Test
    void testGetOrders_WithFilters() throws Exception {
        // Arrange
        PagedResponse<OrderResponse> response = new PagedResponse<>(
                java.util.Collections.emptyList(),
                1,
                10,
                0L,
                0
        );

        org.mockito.Mockito.when(orderService.getOrders(
                1,
                10,
                OrderStatus.PAID,
                new java.math.BigDecimal("10"),
                new java.math.BigDecimal("100"),
                java.time.LocalDate.parse("2026-01-01"),
                java.time.LocalDate.parse("2026-01-10")
        )).thenReturn(response);

        // Act + Assert
        mockMvc.perform(get("/orders")
                        .param("status", "PAID")
                        .param("minAmount", "10")
                        .param("maxAmount", "100")
                        .param("dateFrom", "2026-01-01")
                        .param("dateTo", "2026-01-10")
                        .param("page", "1")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.totalItems").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));

        // Verify
        org.mockito.Mockito.verify(orderService).getOrders(
                1,
                10,
                OrderStatus.PAID,
                new java.math.BigDecimal("10"),
                new java.math.BigDecimal("100"),
                java.time.LocalDate.parse("2026-01-01"),
                java.time.LocalDate.parse("2026-01-10")
        );
    }


}
