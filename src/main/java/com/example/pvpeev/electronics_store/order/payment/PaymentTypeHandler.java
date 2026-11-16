package com.example.pvpeev.electronics_store.order.payment;


import com.example.pvpeev.electronics_store.order.dto.internal.OrderPaymentDetails;

import java.util.UUID;

public interface PaymentTypeHandler {
    PaymentType getSupportedPaymentType();

    boolean requiresPayment(UUID orderId);

    void handlePayment(OrderPaymentDetails orderRequest);
}
