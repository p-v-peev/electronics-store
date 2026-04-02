package com.example.pvpeev.electronics_store.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class OrderRequest {
    @NotNull(message = "User ID is required")
    private final UUID userId;
    @NotBlank(message = "Shipping address cannot be empty")
    private final String orderAddress;
    @NotBlank(message = "Payment type is required")
    private final String paymentType;
    @NotBlank(message = "Phone number is required")
    private final String phoneNumber;
    @NotBlank(message = "Shipping method is required")
    private final String shippingMethod;


    @NotEmpty(message = "Order must contain at least one product")
    @Valid
    private final List<OrderProductRequest> products;
}
