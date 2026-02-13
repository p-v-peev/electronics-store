package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.dto.PaymentTypeResponse;
import com.example.pvpeev.electronics_store.order.mapper.PaymentTypeMapper;
import com.example.pvpeev.electronics_store.order.payment.PaymentType;
import com.example.pvpeev.electronics_store.order.payment.PaymentTypeHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentTypeService {

    private final PaymentTypeMapper paymentTypeMapper;

    private final Map<PaymentType, PaymentTypeHandler> availableHandlers;

    public PaymentTypeService(PaymentTypeMapper paymentTypeMapper, List<PaymentTypeHandler> handlers) {
        this.paymentTypeMapper = paymentTypeMapper;
        this.availableHandlers = handlers.stream().collect(Collectors.toMap(PaymentTypeHandler::getSupportedPaymentType, Function.identity()));
    }

    public List<PaymentTypeResponse> getAllPaymentTypes() {
        return availableHandlers.keySet().stream().map(paymentTypeMapper::toResponse).toList();
    }
}
