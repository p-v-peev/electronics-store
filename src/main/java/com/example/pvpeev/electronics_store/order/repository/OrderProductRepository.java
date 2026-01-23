package com.example.pvpeev.electronics_store.order.repository;

import com.example.pvpeev.electronics_store.order.entity.OrderProductEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderProductRepository extends CrudRepository<OrderProductEntity, UUID>, PagingAndSortingRepository<OrderProductEntity, UUID> {
}
