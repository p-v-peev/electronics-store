package com.example.pvpeev.electronics_store.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class ProductResponse {
    private final UUID id;
    private final Integer productCategoryId;
    private final Integer productBrandId;
    private final String name;
    private final String description;
    private final int price;
    private final int quantityAvailable;
    private final String thumbnailImageUrl;
}
