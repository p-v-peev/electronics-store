package com.example.pvpeev.electronics_store.order.dto.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class OrderDetails {
    private final UUID orderId;
    private final UUID userId;
    private final Integer totalPrice;
    private final Integer shippingMethod;
    private final Integer paymentType;
    private final String previousOrderStatus;
}
