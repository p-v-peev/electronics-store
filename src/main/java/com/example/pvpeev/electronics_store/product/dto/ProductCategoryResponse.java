package com.example.pvpeev.electronics_store.product.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ProductCategoryResponse {
    private final Integer id;
    private final String name;
    private final String description;
}
