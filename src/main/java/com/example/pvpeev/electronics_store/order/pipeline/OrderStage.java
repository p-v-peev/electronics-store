package com.example.pvpeev.electronics_store.order.pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStage {

    ACCEPTED("ACCEPTED"),
    WAITING_WAREHOUSE("WAITING_WAREHOUSE"),
    ARRANGE_SHIPPING("ARRANGE_SHIPPING"),
    ORDER_STATUS_UPDATE("ORDER_STATUS_UPDATE"),
    PROCESS_PAYMENT("PROCESS_PAYMENT"),
    INVALID("INVALID"),
    CANCELED("CANCELED");

    private final String stage;
}
