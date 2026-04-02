package com.example.pvpeev.electronics_store.order.dto.internal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class OrderShippingDetails {
    private final UUID orderId;
    private final Integer shippingMethod;
}
