package com.example.pvpeev.electronics_store.order.status;

import com.example.pvpeev.electronics_store.order.payment.PaymentTypeHandler;
import com.example.pvpeev.electronics_store.order.service.PaymentService;
import com.example.pvpeev.electronics_store.order.service.PaymentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProcessPaymentStageHandler {

    private final PaymentService paymentService;


    @KafkaListener(topics = "#T{(com.example.pvpeev.electronics_store.order.pipeline.OrderStage).PROCESS_PAYMENT.getStage()}",
            batch = "true",
            groupId = "payment-group")
    public void processPayment(List<UUID> ordersIds) {
        paymentService.handlePayment(ordersIds);
    }
}
