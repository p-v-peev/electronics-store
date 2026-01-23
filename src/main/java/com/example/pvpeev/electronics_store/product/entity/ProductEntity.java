package com.example.pvpeev.electronics_store.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID productCategoryId;

    private UUID productBrandId;

    private String name;

    private String description;

    private int price;

    private int quantityAvailable;

    @ElementCollection
    @CollectionTable(name = "product_image", joinColumns = {@JoinColumn(name = "productId")})
    @Column(name = "imageUrl")
    private List<String> productImages;


}
