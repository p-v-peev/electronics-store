package com.example.pvpeev.electronics_store.order.repository;

import com.example.pvpeev.electronics_store.order.entity.OrderEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends CrudRepository<OrderEntity, UUID>, PagingAndSortingRepository<OrderEntity, UUID> {
    @Modifying
    @Query("""
            UPDATE public.order SET tracking_code = :trackingCode WHERE id = :orderId
            """)
    void setOrderTrackingCode(UUID orderId, UUID trackingCode);
}
