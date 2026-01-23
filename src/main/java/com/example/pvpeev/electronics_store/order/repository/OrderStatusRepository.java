package com.example.pvpeev.electronics_store.order.repository;

import com.example.pvpeev.electronics_store.order.entity.OrderStatusEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderStatusRepository extends CrudRepository<OrderStatusEntity, UUID> {
}
