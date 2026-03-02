package com.example.pvpeev.electronics_store.order.status;

import com.example.pvpeev.electronics_store.order.dto.internal.OrderShippingDetails;
import com.example.pvpeev.electronics_store.order.entity.OrderStatusEntity;
import com.example.pvpeev.electronics_store.order.repository.OrderRepository;
import com.example.pvpeev.electronics_store.order.repository.OrderStatusRepository;
import com.example.pvpeev.electronics_store.order.shipping.ShippingMethodHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.pvpeev.electronics_store.order.status.OrderStatus.READY_FOR_SHIPPING;
import static com.example.pvpeev.electronics_store.order.status.OrderStatus.WAITING_FOR_PICKUP;

@Component
public class ArrangeShippingHandler {

    private final OrderStatusRepository orderStatusRepository;
    private final OrderRepository orderRepository;
    private final Map<Integer, ShippingMethodHandler> shippingHandlers;

    public ArrangeShippingHandler(OrderStatusRepository orderStatusRepository, OrderRepository orderRepository, List<ShippingMethodHandler> shippingHandlers) {
        this.orderStatusRepository = orderStatusRepository;
        this.orderRepository = orderRepository;
        this.shippingHandlers = shippingHandlers.stream().collect(Collectors.toMap(sh -> sh.getSupportedShippingMethod().getId(), Function.identity()));
    }

    @KafkaListener(topics = "ARRANGE_SHIPPING", groupId = "shipping-group")
    @Transactional
    public void arrangeShipping(OrderShippingDetails details) {
        orderStatusRepository.save(new OrderStatusEntity(null, details.getOrderId(), READY_FOR_SHIPPING.getId()));
        final ShippingMethodHandler shippingMethodHandler = shippingHandlers.get(details.getShippingMethod());
        final UUID trackingCode = shippingMethodHandler.registerOrder(details.getOrderId());
        orderRepository.setOrderTrackingCode(details.getOrderId(), trackingCode);
        orderStatusRepository.save(new OrderStatusEntity(null, details.getOrderId(), WAITING_FOR_PICKUP.getId()));
    }
}
