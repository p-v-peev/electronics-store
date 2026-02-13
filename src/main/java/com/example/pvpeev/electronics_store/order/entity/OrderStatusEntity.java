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
    private final Long id;

    private final UUID orderId;

    private final Integer orderStatus;

    private final ZonedDateTime statusUpdateDate;
}
