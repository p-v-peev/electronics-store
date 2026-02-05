package com.example.pvpeev.electronics_store.product.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class ProductImageResponse {
    private final UUID id;
    private final UUID productId;
    private final String imageUrl;
}
