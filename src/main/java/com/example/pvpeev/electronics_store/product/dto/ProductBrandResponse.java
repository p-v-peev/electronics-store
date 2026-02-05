package com.example.pvpeev.electronics_store.product.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class ProductBrandResponse {
    private final Integer id;
    private final String name;
}
