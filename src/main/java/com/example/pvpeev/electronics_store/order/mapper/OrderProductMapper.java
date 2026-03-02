package com.example.pvpeev.electronics_store.order.mapper;

import com.example.pvpeev.electronics_store.order.dto.OrderProductRequest;
import com.example.pvpeev.electronics_store.order.entity.OrderProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public interface OrderProductMapper {
    @Mapping(target = "id", ignore = true)
    OrderProductEntity toEntity(OrderProductRequest request, UUID orderId, Integer priceAtPurchase);
}
