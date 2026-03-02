package com.example.pvpeev.electronics_store.order.dto.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class OrderShippingDetails {
    private final UUID orderId;
    private final Integer shippingMethod;
}
