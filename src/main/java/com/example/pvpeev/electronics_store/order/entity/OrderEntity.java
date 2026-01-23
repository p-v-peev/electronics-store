package com.example.pvpeev.electronics_store.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;
import java.util.UUID;

@Table(name = "order")
@RequiredArgsConstructor
@Getter
public class OrderEntity {

    @Id
    private final UUID id;

    private final UUID userId;

    private final String orderAddress;

    private final String orderZipCode;

    private final ZonedDateTime orderDate;

    private final UUID paymentTypeId;

    private final String phoneNumber;

    private final String trackingCode;

    private final UUID shippingMethodId;
}
