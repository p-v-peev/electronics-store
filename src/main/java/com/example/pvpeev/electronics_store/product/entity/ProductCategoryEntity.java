package com.example.pvpeev.electronics_store.product.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "product_category")
@RequiredArgsConstructor
@Getter
public class ProductCategoryEntity {

    @Id
    private final Integer id;

    private final String path;

    private final String name;

    private final String description;

}
