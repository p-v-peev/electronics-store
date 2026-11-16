package com.example.pvpeev.electronics_store.order.payment;

import com.example.pvpeev.electronics_store.order.dto.internal.OrderPaymentDetails;
import com.example.pvpeev.electronics_store.order.service.OrderStatusService;
import com.example.pvpeev.electronics_store.order.service.ShippingMethodService;
import com.example.pvpeev.electronics_store.order.shipping.ShippingMethodHandler;
import com.example.pvpeev.electronics_store.order.status.OrderStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentOnDeliveryHandler extends AbstractPaymentHandler {

    private final OrderStatusService orderStatusService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ShippingMethodService shippingMethodService;

    public PaymentOnDeliveryHandler(OrderStatusService orderStatusService, KafkaTemplate<String, String> kafkaTemplate, ShippingMethodService shippingMethodService) {
        super(orderStatusService);
        this.orderStatusService = orderStatusService;
        this.kafkaTemplate = kafkaTemplate;
        this.shippingMethodService = shippingMethodService;
    }

    @Override
    public PaymentType getSupportedPaymentType() {
        return PaymentType.PAYMENT_ON_DELIVERY;
    }

    @Override
    public void handlePayment(OrderPaymentDetails order) {
        if (super.requiresPayment(order.getOrderId())) {
            final ShippingMethodHandler shippingMethodHandler = shippingMethodService.getHandlerById(order.getShippingMethod());
            final UUID uuid = shippingMethodHandler.requestOrderPayment(order.getOrderId(), order.getTotalPrice());
            // TODO save the ID in Order Payment Log
            orderStatusService.waitingPayment(order.getOrderId());
        } else {
            kafkaTemplate.send(OrderStatus.WAITING_CONFIRMATION.name(), order.getOrderId().toString());
            orderStatusService.waitingConfirmation(order.getOrderId());
        }
    }
}
