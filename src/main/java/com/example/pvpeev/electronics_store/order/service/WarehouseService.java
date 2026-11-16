package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.entity.WarehouseOrderEntity;
import com.example.pvpeev.electronics_store.order.repository.WarehouseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseOrderRepository warehouseOrderRepository;


    public void persistNew(UUID orderId) {
        warehouseOrderRepository.save(new WarehouseOrderEntity(null, orderId, null));
    }
}
