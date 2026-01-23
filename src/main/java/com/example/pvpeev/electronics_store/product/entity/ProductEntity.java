package com.example.pvpeev.electronics_store.product.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "product")
@RequiredArgsConstructor
@Getter
public class ProductEntity {

    @Id
    private final UUID id;

    private final UUID productCategoryId;

    private final UUID productBrandId;

    private final String name;

    private final String description;

    private final int price;

    private final int quantityAvailable;
}
