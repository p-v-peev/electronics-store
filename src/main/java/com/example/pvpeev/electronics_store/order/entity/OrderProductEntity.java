package com.example.pvpeev.electronics_store.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "order_product")
@RequiredArgsConstructor
@Getter
public class OrderProductEntity {

    @Id
    private final UUID id;

    private final UUID orderId;

    private final UUID productId;

    private final int quantity;

    private final int priceAtPurchase;

}
