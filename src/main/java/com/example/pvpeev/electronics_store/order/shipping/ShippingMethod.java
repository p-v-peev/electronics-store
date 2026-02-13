package com.example.pvpeev.electronics_store.order.shipping;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ShippingMethod {

    DHL(1, "DHL"),
    SPEEDY(2, "Speedy"),
    BOX_NOW(3, "BOX_NOW");

    private final Integer id;
    private final String name;
}
