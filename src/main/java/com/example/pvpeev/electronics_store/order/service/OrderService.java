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
import com.example.pvpeev.electronics_store.order.pipeline.OrderPipelineStage;
import com.example.pvpeev.electronics_store.order.repository.OrderRepository;
import com.example.pvpeev.electronics_store.order.status.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final PaymentTypeService paymentTypeService;
    private final ShippingMethodService shippingMethodService;

    private final Supplier<UUID> uuidSupplier;
    private final Clock timeSupplier;

    public OrderResponse findById(UUID orderId) {
        final Optional<OrderEntity> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        return orderMapper.toResponse(order.get());
    }

    public CompletableFuture<UUID> ingestOrder(OrderRequest order) {
        paymentTypeService.getPaymentTypeByName(order.getPaymentType()).orElseThrow(BadRequestException::new);
        shippingMethodService.getShippingMethodByName(order.getShippingMethod()).orElseThrow(BadRequestException::new);

        final UUID orderId = uuidSupplier.get();

        return kafkaTemplate.send(OrderPipelineStage.ACCEPTED.getStage(), new OrderRequestWithId(orderId, order, timeSupplier.instant()))
                .thenApply(ignore -> orderId);
    }

    public void confirmOrder(UUID orderId) {
        this.findById(orderId);
        kafkaTemplate.send(OrderPipelineStage.WAITING_WAREHOUSE.getStage(), orderId.toString());
    }

    public void arrangeShipping(UUID orderId) {
        final OrderResponse orderResponse = this.findById(orderId);
        kafkaTemplate.send(OrderPipelineStage.ARRANGE_SHIPPING.getStage(), new OrderShippingDetails(orderId, orderResponse.getShippingMethod()));
    }

    public void updateStatus(UUID orderId, ShipmentStatusUpdate update) throws ExecutionException, InterruptedException {
        this.findById(orderId);
        final OrderStatus orderStatus = OrderStatus.valueOf(update.getShippingStatus());
        if (orderStatus != OrderStatus.SHIPPED && orderStatus != OrderStatus.OUT_FOR_DELIVERY && orderStatus != OrderStatus.DELIVERED) {
            throw new BadRequestException();
        }

        kafkaTemplate.send(OrderPipelineStage.ORDER_STATUS_UPDATE.getStage(), new OrderStatusDetails(orderId, update.getShippingStatus()));
    }
}
