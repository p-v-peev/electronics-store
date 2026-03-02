package com.example.pvpeev.electronics_store.order.shipping;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ShippingMethod {

    DHL(1, "DHL"),
    SPEEDY(2, "Speedy");

    private final Integer id;
    private final String name;
}
