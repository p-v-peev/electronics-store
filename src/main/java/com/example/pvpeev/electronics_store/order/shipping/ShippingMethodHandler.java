package com.example.pvpeev.electronics_store.order.shipping;

import java.util.UUID;

public interface ShippingMethodHandler {
    ShippingMethod getSupportedShippingMethod();

    UUID registerOrder(UUID orderId);

    UUID requestOrderPayment(Integer amount);
}
