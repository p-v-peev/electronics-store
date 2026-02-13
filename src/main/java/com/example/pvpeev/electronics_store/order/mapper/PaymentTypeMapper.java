package com.example.pvpeev.electronics_store.order.mapper;

import com.example.pvpeev.electronics_store.order.payment.PaymentType;
import com.example.pvpeev.electronics_store.order.dto.PaymentTypeResponse;
import org.mapstruct.Mapper;

@Mapper
public interface PaymentTypeMapper {
    PaymentTypeResponse toResponse(PaymentType paymentType);
}
