package com.example.pvpeev.electronics_store.order.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class OrderStatusResponse {
    private final Integer id;
    private final String displayName;
}
