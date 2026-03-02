package com.example.pvpeev.electronics_store.order.status;

import com.example.pvpeev.electronics_store.order.dto.internal.OrderDetails;
import com.example.pvpeev.electronics_store.order.payment.PaymentTypeHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProcessPaymentStatusHandler {

    private final Map<Integer, PaymentTypeHandler> paymentHandlers;

    public ProcessPaymentStatusHandler(List<PaymentTypeHandler> paymentHandlers) {
        this.paymentHandlers = paymentHandlers.stream().collect(Collectors.toMap(pth -> pth.getSupportedPaymentType().getId(), Function.identity()));
    }


    @KafkaListener(topics = "PROCESS_PAYMENT", groupId = "payment-group")
    public void processPayment(OrderDetails order) {
        final PaymentTypeHandler paymentTypeHandler = paymentHandlers.get(order.getPaymentType());
        paymentTypeHandler.handlePayment(order);
    }
}
