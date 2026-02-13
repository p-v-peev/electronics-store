package com.example.pvpeev.electronics_store.order.mapper;

import com.example.pvpeev.electronics_store.order.status.OrderStatus;
import com.example.pvpeev.electronics_store.order.dto.OrderStatusResponse;
import org.mapstruct.Mapper;

@Mapper
public interface OrderStatusMapper {
    OrderStatusResponse toResponse(OrderStatus orderStatus);
}
