package com.example.pvpeev.electronics_store.order.payment;


import com.example.pvpeev.electronics_store.order.dto.internal.OrderDetails;

public interface PaymentTypeHandler {
    PaymentType getSupportedPaymentType();

    void handlePayment(OrderDetails orderRequest);
}
