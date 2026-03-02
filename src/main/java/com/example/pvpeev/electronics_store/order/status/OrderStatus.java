package com.example.pvpeev.electronics_store.order.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {
    ACCEPTED(100, "Order accepted"),
    WAITING_CONFIRMATION(200, "Waiting confirmation"),
    CONFIRMED(300, "Order confirmed"),
    WAITING_WAREHOUSE(400, "Waiting warehouse"),
    READY_FOR_SHIPPING(500, "Ready for shipping"),
    WAITING_FOR_PICKUP(600, "Waiting for pickup"),
    SHIPPED(700, "Order shipped"),
    OUT_FOR_DELIVERY(800, "Out for delivery"),
    DELIVERED(900, "Order delivered"),
    WAITING_PAYMENT(1000, "Waiting payment"),
    PAID(1100, "Order paid"),
    COMPLETED(1200, "Order completed"),
    REQUIRES_HUMAN_INTERVENTION(1300, "Requires human intervention");


    private final Integer id;
    private final String name;
}
