package com.example.pvpeev.electronics_store.product.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class ProductCategoryResponse {
    private final Integer id;
    private final String path;
    private final String name;
    private final String description;
}
