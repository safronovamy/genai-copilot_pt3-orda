package com.example.orders.dto;


import com.example.orders.model.Order;
import com.example.orders.model.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;
@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String customerName;
    private OrderStatus status;
    private BigDecimal amount;
    private Instant createdAt;
    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .status(order.getStatus())
                .amount(order.getAmount())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
