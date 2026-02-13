package com.example.pvpeev.electronics_store.order.mapper;

import com.example.pvpeev.electronics_store.order.dto.OrderProductRequest;
import com.example.pvpeev.electronics_store.order.entity.OrderProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OrderProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "priceAtPurchase", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    OrderProductEntity toEntity(OrderProductRequest request);
}
