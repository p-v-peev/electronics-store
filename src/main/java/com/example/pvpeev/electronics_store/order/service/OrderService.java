package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.advice.exception.BadRequestException;
import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.order.dto.OrderRequest;
import com.example.pvpeev.electronics_store.order.dto.OrderResponse;
import com.example.pvpeev.electronics_store.order.dto.ShipmentStatusUpdate;
import com.example.pvpeev.electronics_store.order.dto.internal.OrderRequestWithId;
import com.example.pvpeev.electronics_store.order.dto.internal.OrderShippingDetails;
import com.example.pvpeev.electronics_store.order.dto.internal.OrderStatusDetails;
import com.example.pvpeev.electronics_store.order.entity.OrderEntity;
import com.example.pvpeev.electronics_store.order.mapper.OrderMapper;
import com.example.pvpeev.electronics_store.order.payment.PaymentType;
import com.example.pvpeev.electronics_store.order.repository.OrderRepository;
import com.example.pvpeev.electronics_store.order.shipping.ShippingMethod;
import com.example.pvpeev.electronics_store.order.status.OrderStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.example.pvpeev.electronics_store.order.status.OrderStatus.*;

@Service
public class OrderService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    public OrderService(KafkaTemplate<String, Object> kafkaTemplate, OrderMapper orderMapper, OrderRepository orderRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
    }

    public OrderResponse findById(UUID orderId) {
        final Optional<OrderEntity> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        return orderMapper.toResponse(order.get());
    }

    public CompletableFuture<UUID> ingestOrder(OrderRequest order) {
        try {
            PaymentType.valueOf(order.getPaymentType());
            ShippingMethod.valueOf(order.getShippingMethod());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException();
        }
        final UUID orderId = UUID.randomUUID();
        return kafkaTemplate.send(ACCEPTED.name(), new OrderRequestWithId(orderId, order))
                .thenApply(ignore -> orderId);
    }

    @Transactional
    public void confirmOrder(UUID orderId) {
        final boolean orderExists = orderRepository.existsById(orderId);
        if (!orderExists) {
            throw new ResourceNotFoundException();
        }
        kafkaTemplate.send(WAITING_WAREHOUSE.name(), orderId.toString());
    }

    @Transactional
    public void arrangeShipping(UUID orderId) {
        final Optional<OrderEntity> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        kafkaTemplate.send("ARRANGE_SHIPPING", new OrderShippingDetails(orderId, order.get().getShippingMethod()));
    }

    public void updateStatus(UUID orderId, ShipmentStatusUpdate update) throws ExecutionException, InterruptedException {
        final Optional<OrderEntity> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        final OrderStatus orderStatus = OrderStatus.valueOf(update.getShippingStatus());
        if (orderStatus != SHIPPED && orderStatus != OUT_FOR_DELIVERY && orderStatus != DELIVERED) {
            throw new BadRequestException();
        }

        kafkaTemplate.send("ORDER_STATUS_UPDATE", new OrderStatusDetails(orderId, update.getShippingStatus()));
    }
}
