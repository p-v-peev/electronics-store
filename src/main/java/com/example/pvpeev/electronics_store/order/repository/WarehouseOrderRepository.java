package com.example.pvpeev.electronics_store.order.repository;

import com.example.pvpeev.electronics_store.order.entity.WarehouseOrderEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WarehouseOrderRepository extends CrudRepository<WarehouseOrderEntity, Long> {
    void deleteByOrderId(UUID orderId);
}
