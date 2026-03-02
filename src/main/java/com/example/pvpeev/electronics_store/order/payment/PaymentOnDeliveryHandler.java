package com.example.pvpeev.electronics_store.order.payment;

import com.example.pvpeev.electronics_store.order.dto.internal.OrderDetails;
import com.example.pvpeev.electronics_store.order.entity.OrderStatusEntity;
import com.example.pvpeev.electronics_store.order.repository.OrderProductRepository;
import com.example.pvpeev.electronics_store.order.repository.OrderStatusRepository;
import com.example.pvpeev.electronics_store.order.shipping.ShippingMethodHandler;
import com.example.pvpeev.electronics_store.order.status.OrderStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentOnDeliveryHandler implements PaymentTypeHandler {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderProductRepository orderProductRepository;
    private final Map<Integer, ShippingMethodHandler> shippingHandlers;

    public PaymentOnDeliveryHandler(KafkaTemplate<String, String> kafkaTemplate, OrderStatusRepository orderStatusRepository, OrderProductRepository orderProductRepository, List<ShippingMethodHandler> shippingHandlers) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderStatusRepository = orderStatusRepository;
        this.orderProductRepository = orderProductRepository;
        this.shippingHandlers = shippingHandlers.stream().collect(Collectors.toMap(sh -> sh.getSupportedShippingMethod().getId(), Function.identity()));
    }

    @Override
    public PaymentType getSupportedPaymentType() {
        return PaymentType.PAYMENT_ON_DELIVERY;
    }

    @Override
    public void handlePayment(OrderDetails order) {
        final OrderStatus orderStatus = OrderStatus.valueOf(order.getPreviousOrderStatus());
        if (OrderStatus.ACCEPTED == orderStatus) {
            kafkaTemplate.send(OrderStatus.WAITING_CONFIRMATION.name(), order.getOrderId().toString());
            orderStatusRepository.save(new OrderStatusEntity(null, order.getOrderId(), OrderStatus.WAITING_CONFIRMATION.getId()));
        } else if (OrderStatus.DELIVERED == orderStatus) {
            final ShippingMethodHandler shippingMethodHandler = shippingHandlers.get(order.getShippingMethod());
            final UUID uuid = shippingMethodHandler.requestOrderPayment(orderProductRepository.getTotalOrderPrice(order.getOrderId()));
            orderStatusRepository.save(new OrderStatusEntity(null, order.getOrderId(), OrderStatus.WAITING_PAYMENT.getId()));
        }
    }
}
