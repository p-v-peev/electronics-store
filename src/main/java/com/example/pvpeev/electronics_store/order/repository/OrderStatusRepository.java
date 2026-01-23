package com.example.pvpeev.electronics_store.order.repository;

import com.example.pvpeev.electronics_store.order.entity.OrderStatusEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusRepository extends CrudRepository<OrderStatusEntity, OrderStatusEntity.OrderStatusId> {
}
