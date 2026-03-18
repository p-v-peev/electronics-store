package com.example.pvpeev.electronics_store.order.mapper;

import com.example.pvpeev.electronics_store.order.dto.OrderRequest;
import com.example.pvpeev.electronics_store.order.dto.OrderResponse;
import com.example.pvpeev.electronics_store.order.entity.OrderEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper
public interface OrderMapper {
    @Mapping(target = "trackingCode", ignore = true)
    @Mapping(target = "paymentType", expression = "java(com.example.pvpeev.electronics_store.order.payment.PaymentType.valueOf(request.getPaymentType()).getId())")
    @Mapping(target = "shippingMethod", expression = "java(com.example.pvpeev.electronics_store.order.shipping.ShippingMethod.valueOf(request.getShippingMethod()).getId())")
    OrderEntity toNewEntity(UUID id, OrderRequest request);

    @AfterMapping
    default OrderEntity markEntityAsNew(@MappingTarget OrderEntity entity) {
        return entity.setNew();
    }

    OrderResponse toResponse(OrderEntity entity);
}
