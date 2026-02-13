package com.example.pvpeev.electronics_store.order.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PaymentTypeResponse {
    private final Integer id;
    private final String name;
    private final String description;
}
