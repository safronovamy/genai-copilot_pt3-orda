package com.example.orders.dto;

// Create DTO OrderResponse used in API responses.
// Fields:
// - id: Long
// - customerName: String
// - status: OrderStatus
// - amount: BigDecimal
// - createdAt: Instant
//
// Requirements:
// - Provide a static factory method: fromEntity(Order order) -> OrderResponse
// - Keep it simple and immutable if possible (e.g., Lombok @Builder + @Getter), but consistency matters.
// - Import com.example.orders.model.Order and OrderStatus.
// - Package: com.example.orders.dto
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
