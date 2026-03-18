package com.example.pvpeev.electronics_store.order.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class ShippingMethodResponse {
    private final Integer id;
    private final String name;
}
