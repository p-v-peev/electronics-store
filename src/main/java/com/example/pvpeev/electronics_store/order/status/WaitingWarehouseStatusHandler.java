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

import static com.example.pvpeev.electronics_store.order.status.OrderStatus.CONFIRMED;
import static com.example.pvpeev.electronics_store.order.status.OrderStatus.WAITING_WAREHOUSE;

@Component
@RequiredArgsConstructor
public class WaitingWarehouseStatusHandler {

    private final OrderStatusRepository orderStatusRepository;
    private final WarehouseOrderRepository warehouseOrderRepository;

    @KafkaListener(topics = "WAITING_WAREHOUSE", groupId = "warehouse-group")
    @Transactional
    public void processPlacedOrders(UUID orderId) {
        orderStatusRepository.save(new OrderStatusEntity(null, orderId, CONFIRMED.getId()));
        warehouseOrderRepository.save(new WarehouseOrderEntity(null, orderId, null));
        orderStatusRepository.save(new OrderStatusEntity(null, orderId, WAITING_WAREHOUSE.getId()));
    }
}
