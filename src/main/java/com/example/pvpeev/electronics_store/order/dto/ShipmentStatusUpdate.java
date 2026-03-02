package com.example.pvpeev.electronics_store.order.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ShipmentStatusUpdate {
    private final String shippingStatus;
}
