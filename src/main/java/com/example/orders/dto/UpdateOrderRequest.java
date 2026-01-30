package com.example.orders.dto;

import com.example.orders.model.OrderStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class UpdateOrderRequest {

    @Size(max = 255, message = "Customer name must be <= 255 characters")
    private String customerName;

    @DecimalMin(value = "0.00", inclusive = false, message = "Amount must be > 0")
    private BigDecimal amount;

    private OrderStatus status;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
