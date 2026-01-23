package com.example.pvpeev.electronics_store.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;
import java.util.UUID;

@Table(name = "order_status")
@RequiredArgsConstructor
@Getter
public class OrderStatusEntity {

    @Id
    private final UUID id;

    private final UUID orderId;

    private final UUID deliveryStatusId;

    private final ZonedDateTime statusUpdateDate;

    private final String statusDescription;
}
