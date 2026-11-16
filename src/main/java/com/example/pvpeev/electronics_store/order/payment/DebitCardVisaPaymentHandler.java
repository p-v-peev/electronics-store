package com.example.pvpeev.electronics_store.order.payment;

import com.example.pvpeev.electronics_store.order.dto.internal.OrderPaymentDetails;
import com.example.pvpeev.electronics_store.order.service.OrderStatusService;
import com.example.pvpeev.electronics_store.order.service.WarehouseService;
import com.example.pvpeev.electronics_store.order.status.OrderStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DebitCardVisaPaymentHandler extends AbstractPaymentHandler {

    private final OrderStatusService orderStatusService;
    private final WarehouseService warehouseService;
    private final JdbcTemplate jdbcTemplate;

    public DebitCardVisaPaymentHandler(OrderStatusService orderStatusService, WarehouseService warehouseService, JdbcTemplate jdbcTemplate) {
        super(orderStatusService);
        this.orderStatusService = orderStatusService;
        this.warehouseService = warehouseService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PaymentType getSupportedPaymentType() {
        return PaymentType.DEBIT_CARD_VISA;
    }

    @Override
    @Transactional
    public void handlePayment(OrderPaymentDetails order) {
        if (super.requiresPayment(order.getOrderId())) {
            final UUID uuid = requestOrderPayment(order.getOrderId(), order.getTotalPrice());
            // TODO save the ID in Order Payment Log
            final Map<UUID, Integer> parameters = Stream.of(OrderStatus.CONFIRMED.getId(), OrderStatus.PAID.getId(), OrderStatus.WAITING_WAREHOUSE.getId())
                    .collect(Collectors.toMap(ignore -> order.getOrderId(), Function.identity()));
            jdbcTemplate.batchUpdate("""
                            INSERT INTO order_status (order_id, order_status)
                            VALUES (?, ?)
                            """,
                    parameters.entrySet(),
                    parameters.size(),
                    (ps, argument) -> {
                        ps.setObject(1, argument.getKey());
                        ps.setInt(2, argument.getValue());
                    });
            warehouseService.persistNew(order.getOrderId());
        } else {
            orderStatusService.completeOrder(order.getOrderId());
        }
    }

    private UUID requestOrderPayment(UUID orderId, Integer amount) {
        System.out.printf("Requesting payment of: %d for order: %s", amount, orderId.toString());
        return UUID.randomUUID();
    }
}
