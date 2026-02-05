package com.example.pvpeev.electronics_store.product.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "product")
@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class ProductEntity {

    @Id
    private final UUID id;

    private final Integer productCategoryId;

    private final Integer productBrandId;

    private final String name;

    private final String description;

    private final int price;

    private final int quantityAvailable;

    private final String thumbnailImageUrl;

}
