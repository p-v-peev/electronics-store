package com.example.pvpeev.electronics_store.order.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {
    ACCEPTED(100, true, "Order accepted"),
    WAITING_CONFIRMATION(200, true, "Waiting confirmation"),
    CONFIRMED(300, true, "Order confirmed"),
    WAITING_WAREHOUSE(400, true, "Waiting warehouse"),
    READY_FOR_SHIPPING(500, true, "Ready for shipping"),
    WAITING_FOR_PICKUP(600, true, "Waiting for pickup"),
    SHIPPED(700, false, "Order shipped"),
    OUT_FOR_DELIVERY(800, false, "Out for delivery"),
    DELIVERED(900, false, "Order delivered"),
    WAITING_PAYMENT(1000, true, "Waiting payment"),
    PAID(1100, true, "Order paid"),
    COMPLETED(1200, true, "Order completed"),
    REQUIRES_HUMAN_INTERVENTION(1300, true, "Requires human intervention"),
    CANCELED(1400, true, "Canceled");


    private final Integer id;
    private final boolean internal;
    private final String displayName;
}
