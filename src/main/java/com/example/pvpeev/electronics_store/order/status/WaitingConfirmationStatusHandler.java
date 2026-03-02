package com.example.pvpeev.electronics_store.order.status;

import com.example.pvpeev.electronics_store.order.entity.OrderStatusEntity;
import com.example.pvpeev.electronics_store.order.entity.WarehouseOrderEntity;
import com.example.pvpeev.electronics_store.order.repository.OrderStatusRepository;
import com.example.pvpeev.electronics_store.order.repository.WarehouseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WaitingConfirmationStatusHandler {

    private final OrderStatusRepository orderStatusRepository;
    private final WarehouseOrderRepository warehouseOrderRepository;


    @KafkaListener(topics = "WAITING_CONFIRMATION", groupId = "waiting-confirmation-group")
    @Transactional
    public void processPlacedOrders(UUID orderId) {
        // TODO implement some confirmation conditions:
        // TODO for users with good rating but the order has many items email confirmation
        // TODO for expensive orders phone call
        // For simplicity autoconfirm
        orderStatusRepository.save(new OrderStatusEntity(null, orderId, OrderStatus.CONFIRMED.getId()));
        warehouseOrderRepository.save(new WarehouseOrderEntity(null, orderId, null));
        orderStatusRepository.save(new OrderStatusEntity(null, orderId, OrderStatus.WAITING_WAREHOUSE.getId()));
    }
}
