package com.example.pvpeev.electronics_store.product.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "product_brand")
@RequiredArgsConstructor
@Getter
public class ProductBrandEntity {

    @Id
    private final UUID id;

    private final String name;
}
