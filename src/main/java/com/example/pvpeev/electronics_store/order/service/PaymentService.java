package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.dto.OrderResponse;
import com.example.pvpeev.electronics_store.order.dto.internal.OrderPaymentDetails;
import com.example.pvpeev.electronics_store.order.payment.PaymentTypeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderService orderService;
    private final OrderProductService productService;
    private final PaymentTypeService paymentTypeService;

    public void handlePayment(UUID orderId) {
        final OrderResponse order = orderService.findById(orderId);
        final int totalPrice = productService.getTotalPrice(order.getId());
        final OrderPaymentDetails orderPaymentDetails = new OrderPaymentDetails(order.getId(), order.getUserId(), totalPrice, order.getShippingMethod(), order.getPaymentType());
        final PaymentTypeHandler handler = paymentTypeService.getPaymentTypeHandlerByPaymentTypeId(order.getPaymentType());
        handler.handlePayment(orderPaymentDetails);
    }
}
