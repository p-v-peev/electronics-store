package com.example.pvpeev.electronics_store.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "warehouse_queue")
@RequiredArgsConstructor
@Getter
public class WarehouseOrderEntity {
    @Id
    private final Long id;

    private final UUID orderId;

    private final Integer binId;
}
