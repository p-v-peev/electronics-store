package com.example.pvpeev.electronics_store.order.shipping;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SpeedyShippingMethodHandler implements ShippingMethodHandler {
    @Override
    public ShippingMethod getSupportedShippingMethod() {
        return ShippingMethod.SPEEDY;
    }

    @Override
    public String registerOrder(UUID orderId) {
        return UUID.randomUUID().toString();
    }

    @Override
    public UUID requestOrderPayment(Integer amount) {
        System.out.println("Requesting payment " + getSupportedShippingMethod().getName() + " " + amount);
        return UUID.randomUUID();
    }
}
