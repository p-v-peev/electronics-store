package com.example.pvpeev.electronics_store.product.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ProductCategoryRequest {
    private final String path;
    private final String name;
    private final String description;
}
