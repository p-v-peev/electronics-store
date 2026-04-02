package com.example.pvpeev.electronics_store.order.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class OrderResponse {
    private final UUID id;
    private final UUID userId;
    private final String orderAddress;
    private final Integer paymentType;
    private final String phoneNumber;
    private final String trackingCode;
    private final Integer shippingMethod;
}
