package com.example.pvpeev.electronics_store.order.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class OrderProductRequest {
    private final UUID productId;
    private final Integer quantity;
}
