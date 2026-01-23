package com.example.pvpeev.electronics_store.order.entity;

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
@Table(name = "order_product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderProductEntity {

    @EmbeddedId
    private OrderProductId id;

    private int quantity;

    private int priceAtPurchase;


    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class OrderProductId implements Serializable {
        private UUID orderId;
        private UUID productId;
    }
}
