package com.example.pvpeev.electronics_store.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class OrderProductRequest {
    @NotNull(message = "Product ID is required")
    private final UUID productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10, message = "For bulk orders over 10, please contact sales")
    private final Integer quantity;
}
