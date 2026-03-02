package com.example.pvpeev.electronics_store.order.status;

import com.example.pvpeev.electronics_store.order.dto.internal.OrderDetails;
import com.example.pvpeev.electronics_store.order.dto.internal.OrderRequestWithId;
import com.example.pvpeev.electronics_store.order.payment.PaymentTypeHandler;
import com.example.pvpeev.electronics_store.order.service.OrderPersistenceService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AcceptedStatusHandler {
    private final OrderPersistenceService orderPersistenceService;
    private final KafkaTemplate<String, OrderDetails> kafkaTemplate;
    private final Map<String, PaymentTypeHandler> paymentHandlers;

    public AcceptedStatusHandler(OrderPersistenceService orderPersistenceService, KafkaTemplate<String, OrderDetails> kafkaTemplate, List<PaymentTypeHandler> paymentHandlers) {
        this.orderPersistenceService = orderPersistenceService;
        this.kafkaTemplate = kafkaTemplate;
        this.paymentHandlers = paymentHandlers.stream().collect(Collectors.toMap(pth -> pth.getSupportedPaymentType().name(), Function.identity()));
    }

    @KafkaListener(topics = "ACCEPTED", groupId = "accepting-group")
    @Transactional
    public void processPlacedOrders(OrderRequestWithId order) {
        final OrderDetails orderDetails = orderPersistenceService.persistOrder(order);
        kafkaTemplate.send("PROCESS_PAYMENT", orderDetails);
    }
}
