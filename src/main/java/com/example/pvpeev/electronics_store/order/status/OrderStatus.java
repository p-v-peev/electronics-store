package com.example.pvpeev.electronics_store.order.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {

    PLACED(0, "Order placed"),
    ACCEPTED(100, "Order accepted"),
    CONFIRMED(200, "Order confirmed"),
    READY_FOR_SHIPPING(300, "Ready for shipping"),
    SHIPMENT_REGISTERED(400, "Shipment registered"),
    SHIPPING_LABEL_CREATED(500, "Shipping label created"),
    WAITING_FOR_PICKUP(700, "Waiting for pickup"),
    SHIPPED(800, "Order shipped"),
    OUT_FOR_DELIVERY(900, "Out for delivery"),
    DELIVERED(1000, "Order delivered"),
    PAID(1100, "Order paid"),
    COMPLETED(1200, "Order completed"),
    REQUIRES_HUMAN_INTERVENTION(1300, "Requires human intervention");


    private final Integer id;
    private final String name;
}
