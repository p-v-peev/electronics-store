package com.example.pvpeev.electronics_store.product.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "product_image")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageEntity {

    @EmbeddedId
    private ProductImageId id;

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ProductImageId implements Serializable {
        private UUID productId;
        private String imageUrl;
    }
}
