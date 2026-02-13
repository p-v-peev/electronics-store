package com.example.pvpeev.electronics_store.order.mapper;

import com.example.pvpeev.electronics_store.order.dto.OrderRequest;
import com.example.pvpeev.electronics_store.order.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trackingCode", ignore = true)
    OrderEntity toEntity(OrderRequest request);
}
