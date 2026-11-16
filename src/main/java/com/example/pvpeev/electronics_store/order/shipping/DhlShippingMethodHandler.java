package com.example.pvpeev.electronics_store.order.shipping;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DhlShippingMethodHandler implements ShippingMethodHandler {
    @Override
    public ShippingMethod getSupportedShippingMethod() {
        return ShippingMethod.DHL;
    }

    @Override
    public String registerOrder(UUID orderId) {
        return UUID.randomUUID().toString();
    }

    @Override
    public UUID requestOrderPayment(UUID orderId, Integer amount) {
        System.out.printf("Requesting payment of: %d for order: %s", amount, orderId.toString());
        return UUID.randomUUID();
    }
}
