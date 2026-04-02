package com.example.pvpeev.electronics_store.order.pipeline;

import com.example.pvpeev.electronics_store.order.dto.internal.OrderRequestWithId;
import com.example.pvpeev.electronics_store.order.dto.internal.OrderShippingDetails;
import com.example.pvpeev.electronics_store.order.dto.internal.OrderStatusDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public enum OrderPipelineStage {

    ACCEPTED("ACCEPTED", OrderRequestWithId.class),
    WAITING_WAREHOUSE("WAITING_WAREHOUSE", UUID.class),
    ARRANGE_SHIPPING("ARRANGE_SHIPPING", OrderShippingDetails.class),
    ORDER_STATUS_UPDATE("ORDER_STATUS_UPDATE", OrderStatusDetails.class),
    PROCESS_PAYMENT("PROCESS_PAYMENT", Void.class),
    INVALID("INVALID", Void.class),
    CANCELED("CANCELED", Void.class);

    private final String stage;
    private final Class<?> clazz;
}
