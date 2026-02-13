package com.example.pvpeev.electronics_store.order.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OrderStatusResponse {
    private final Integer id;
    private final String name;
}
