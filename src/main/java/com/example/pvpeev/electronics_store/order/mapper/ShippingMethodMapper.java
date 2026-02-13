package com.example.pvpeev.electronics_store.order.mapper;

import com.example.pvpeev.electronics_store.order.shipping.ShippingMethod;
import com.example.pvpeev.electronics_store.order.dto.ShippingMethodResponse;
import org.mapstruct.Mapper;

@Mapper
public interface ShippingMethodMapper {
    ShippingMethodResponse toResponse(ShippingMethod shippingMethod);
}
