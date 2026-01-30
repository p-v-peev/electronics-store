package com.example.pvpeev.electronics_store.product.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "product_brand")
@RequiredArgsConstructor
@Getter
public class ProductBrandEntity {

    @Id
    private final Integer id;

    private final String name;

}
