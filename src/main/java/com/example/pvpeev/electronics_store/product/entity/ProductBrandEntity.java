package com.example.pvpeev.electronics_store.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "product_brand")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductBrandEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
}
