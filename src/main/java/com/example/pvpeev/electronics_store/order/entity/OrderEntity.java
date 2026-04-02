package com.example.pvpeev.electronics_store.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "order")
@RequiredArgsConstructor
@Getter
public class OrderEntity implements Persistable<UUID> {

    @Id
    private final UUID id;

    private final UUID userId;

    private final String orderAddress;

    private final Integer paymentType;

    private final String phoneNumber;

    private final String trackingCode;

    private final Integer shippingMethod;

    @Transient
    private boolean isNew = false;

    public OrderEntity setNew() {
        this.isNew = true;
        return this;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
