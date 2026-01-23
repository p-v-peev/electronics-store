package com.example.pvpeev.electronics_store.order.repository;

import com.example.pvpeev.electronics_store.order.entity.OrderProductEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductRepository extends CrudRepository<OrderProductEntity, OrderProductEntity.OrderProductId>, PagingAndSortingRepository<OrderProductEntity, OrderProductEntity.OrderProductId> {
}
