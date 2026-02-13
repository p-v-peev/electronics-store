package com.example.pvpeev.electronics_store.order.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class OrderRequest {
    private final UUID userId;
    private final String orderAddress;
//    private final ZonedDateTime orderDate;
    private final Integer paymentType;
    private final String phoneNumber;
    private final Integer shippingMethod;
    private final List<OrderProductRequest> products;
}
