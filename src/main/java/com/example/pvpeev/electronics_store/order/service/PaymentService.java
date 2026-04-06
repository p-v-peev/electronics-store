package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.entity.OrderEntity;
import com.example.pvpeev.electronics_store.order.payment.PaymentTypeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentTypeService paymentTypeService;
    private final OrderService orderService;

    private final Map<Integer, PaymentTypeHandler> paymentTypesById;

    public void handlePayment(List<UUID> ordersIds) {
        final List<OrderEntity> allOrders = orderService.findAllById(ordersIds);
        for (OrderEntity order : allOrders) {
            final PaymentTypeHandler handler = paymentTypeService.getPaymentTypeHandlerByPaymentTypeId(order.getPaymentType());
            handler.handlePayment();
        }
    }
}
