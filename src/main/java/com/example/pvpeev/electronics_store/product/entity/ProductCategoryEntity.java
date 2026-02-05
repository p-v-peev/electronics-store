package com.example.pvpeev.electronics_store.product.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "product_category")
@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class ProductCategoryEntity {

    @Id
    private final Integer id;

    private final String path;

    private final String name;

    private final String description;

}
