package com.example.pvpeev.electronics_store.product.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "product_image")
@RequiredArgsConstructor
@Getter
public class ProductImageEntity {

    @Id
    private final UUID id;

    private final UUID productId;

    private final String imageUrl;
}
