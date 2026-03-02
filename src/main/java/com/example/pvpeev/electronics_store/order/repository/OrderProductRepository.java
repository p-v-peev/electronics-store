package com.example.pvpeev.electronics_store.order.repository;

import com.example.pvpeev.electronics_store.order.entity.OrderProductEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderProductRepository extends ListCrudRepository<OrderProductEntity, UUID>, PagingAndSortingRepository<OrderProductEntity, UUID> {
    @Query("SELECT SUM(price_at_purchase) FROM order_product WHERE order_id = :orderId")
    int getTotalOrderPrice(UUID orderId);
}
