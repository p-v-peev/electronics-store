package com.example.pvpeev.electronics_store.order.status;

import com.example.pvpeev.electronics_store.order.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProcessPaymentKafkaListener {

    private final PaymentService paymentService;


    @KafkaListener(topics = "#T{(com.example.pvpeev.electronics_store.order.pipeline.OrderStage).PROCESS_PAYMENT.getStage()}",
            groupId = "payment-group")
    public void processPayment(UUID orderId) {
        paymentService.handlePayment(orderId);
    }
}
