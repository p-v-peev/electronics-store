package com.example.pvpeev.electronics_store.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "delivery_status")
@RequiredArgsConstructor
@Getter
public class DeliveryStatusEntity {

    @Id
    private final UUID id;

    private final String name;
}
