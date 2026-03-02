package com.example.pvpeev.electronics_store.order.status;


import com.example.pvpeev.electronics_store.order.dto.internal.OrderDetails;
import com.example.pvpeev.electronics_store.order.dto.internal.OrderStatusDetails;
import com.example.pvpeev.electronics_store.order.entity.OrderEntity;
import com.example.pvpeev.electronics_store.order.entity.OrderStatusEntity;
import com.example.pvpeev.electronics_store.order.payment.PaymentTypeHandler;
import com.example.pvpeev.electronics_store.order.repository.OrderProductRepository;
import com.example.pvpeev.electronics_store.order.repository.OrderRepository;
import com.example.pvpeev.electronics_store.order.repository.OrderStatusRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.pvpeev.electronics_store.order.status.OrderStatus.DELIVERED;

@Component
public class StatusUpdateStatusHandler {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final Map<Integer, PaymentTypeHandler> paymentHandlers;

    public StatusUpdateStatusHandler(OrderRepository orderRepository, OrderProductRepository orderProductRepository, OrderStatusRepository orderStatusRepository, List<PaymentTypeHandler> paymentHandlers) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.paymentHandlers = paymentHandlers.stream().collect(Collectors.toMap(pth -> pth.getSupportedPaymentType().getId(), Function.identity()));
    }

    @KafkaListener(topics = "ORDER_STATUS_UPDATE", groupId = "order-status-group")
    @Transactional
    public void processStatusUpdate(OrderStatusDetails orderStatusDetails) {

        final Optional<OrderEntity> order = orderRepository.findById(orderStatusDetails.getOrderId());
        if (order.isEmpty()) {
            return;
        }
        final OrderStatus orderStatus = OrderStatus.valueOf(orderStatusDetails.getOrderStatus());
        final OrderEntity orderEntity = order.get();

        if (orderStatus == DELIVERED) {
            final PaymentTypeHandler paymentTypeHandler = paymentHandlers.get(orderEntity.getPaymentType());
            final int totalOrderPrice = orderProductRepository.getTotalOrderPrice(orderStatusDetails.getOrderId());
            final OrderDetails orderDetails = new OrderDetails(orderEntity.getId(), orderEntity.getUserId(), totalOrderPrice, orderEntity.getShippingMethod(), orderEntity.getPaymentType(), DELIVERED.name());
            paymentTypeHandler.handlePayment(orderDetails);
        } else {
            orderStatusRepository.save(new OrderStatusEntity(null, orderStatusDetails.getOrderId(), orderStatus.getId()));
        }
    }
}
