package com.example.pvpeev.electronics_store.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "warehouse_bin")
@RequiredArgsConstructor
@Getter
public class WarehouseBinEntity {

    @Id
    private final Integer id;

    private final String binLabel;

}
