package com.example.pvpeev.electronics_store.product.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ProductRequest {
    private final Integer productBrandId;
    private final String name;
    private final String description;
    private final Integer price;
    private final Integer quantityAvailable;
}
