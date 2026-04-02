package com.example.pvpeev.electronics_store.order.dto.internal;

import com.example.pvpeev.electronics_store.order.dto.OrderRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class OrderRequestWithId {
    private final UUID id;
    private final OrderRequest request;
    private final Instant date;
}
