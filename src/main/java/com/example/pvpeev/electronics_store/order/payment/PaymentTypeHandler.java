package com.example.pvpeev.electronics_store.order.payment;

import com.example.pvpeev.electronics_store.order.entity.OrderEntity;

public interface PaymentTypeHandler {
    PaymentType getSupportedPaymentType();

    void handlePayment(OrderEntity orderEntity);
}
