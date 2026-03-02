package com.example.pvpeev.electronics_store.order.mapper;

import com.example.pvpeev.electronics_store.order.dto.OrderRequest;
import com.example.pvpeev.electronics_store.order.dto.OrderResponse;
import com.example.pvpeev.electronics_store.order.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public interface OrderMapper {
    @Mapping(target = "trackingCode", ignore = true)
    @Mapping(target = "paymentType", expression = "java(com.example.pvpeev.electronics_store.order.payment.PaymentType.valueOf(request.getPaymentType()).getId())")
    @Mapping(target = "shippingMethod", expression = "java(com.example.pvpeev.electronics_store.order.shipping.ShippingMethod.valueOf(request.getShippingMethod()).getId())")
    @Mapping(target = "new", constant = "true")
    OrderEntity toNewEntity(UUID id, OrderRequest request);

    OrderResponse toResponse(OrderEntity entity);
}
