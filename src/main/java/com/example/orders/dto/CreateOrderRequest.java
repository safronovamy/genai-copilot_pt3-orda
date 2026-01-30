package com.example.orders.dto;

// Create DTO CreateOrderRequest for POST /orders.
// Fields:
// - customerName: String, required (@NotBlank)
// - status: OrderStatus, optional (may be null; service will default to NEW)
// - amount: BigDecimal, required (@NotNull, @Positive)
//
// Requirements:
// - Use jakarta.validation annotations.
// - Use BigDecimal for amount.
// - Do NOT include id or createdAt in request.
// - Use Lombok @Getter/@Setter or @Data (consistent with the project).
// - Package: com.example.orders.dto
import com.example.orders.model.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {
    @NotBlank
    private String customerName;
    private OrderStatus status;
    @NotNull
    @Positive
    private BigDecimal amount;
}
