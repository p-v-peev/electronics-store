package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.dto.PaymentTypeResponse;
import com.example.pvpeev.electronics_store.order.mapper.PaymentTypeMapper;
import com.example.pvpeev.electronics_store.order.payment.PaymentType;
import com.example.pvpeev.electronics_store.order.payment.PaymentTypeHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentTypeService {
    private final PaymentTypeMapper paymentTypeMapper;

    private final Map<PaymentType, PaymentTypeHandler> availableHandlers;
    private final Map<String, PaymentType> paymentTypesByName;
    private final Map<Integer, PaymentTypeHandler> paymentTypesById;

    public PaymentTypeService(PaymentTypeMapper paymentTypeMapper, List<PaymentTypeHandler> handlers) {
        this.paymentTypeMapper = paymentTypeMapper;
        this.availableHandlers = handlers.stream().collect(Collectors.toMap(PaymentTypeHandler::getSupportedPaymentType, Function.identity()));
        this.paymentTypesByName = handlers.stream().collect(Collectors.toMap(pth -> pth.getSupportedPaymentType().name(), PaymentTypeHandler::getSupportedPaymentType));
        this.paymentTypesById = handlers.stream().collect(Collectors.toMap(pth -> pth.getSupportedPaymentType().getId(), Function.identity()));
    }

    public List<PaymentTypeResponse> getAllPaymentTypes() {
        return this.availableHandlers.keySet().stream().map(this.paymentTypeMapper::toResponse).toList();
    }

    public Optional<PaymentType> getPaymentTypeByName(String name) {
        return Optional.ofNullable(this.paymentTypesByName.get(name));
    }

    public PaymentTypeHandler getPaymentTypeHandlerByPaymentTypeId(Integer id) {
        return this.paymentTypesById.get(id);
    }
}
