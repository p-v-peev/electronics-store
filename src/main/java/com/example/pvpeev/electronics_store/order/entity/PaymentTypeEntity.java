package com.example.pvpeev.electronics_store.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "payment_type")
@RequiredArgsConstructor
@Getter
public class PaymentTypeEntity {

    @Id
    private final UUID id;

    private final String name;

    private final String description;
}
