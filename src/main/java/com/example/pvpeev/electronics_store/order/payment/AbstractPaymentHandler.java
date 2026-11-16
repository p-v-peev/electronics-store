package com.example.pvpeev.electronics_store.order.payment;

import com.example.pvpeev.electronics_store.order.service.OrderStatusService;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public abstract class AbstractPaymentHandler implements PaymentTypeHandler {

    private final OrderStatusService orderStatusService;

    @Override
    public boolean requiresPayment(UUID orderId) {
        return orderStatusService.hasOrderStatusPayed(orderId);
    }
}
