package com.example.pvpeev.electronics_store.order.payment;

import com.example.pvpeev.electronics_store.order.dto.internal.OrderDetails;
import com.example.pvpeev.electronics_store.order.entity.OrderStatusEntity;
import com.example.pvpeev.electronics_store.order.entity.WarehouseOrderEntity;
import com.example.pvpeev.electronics_store.order.repository.OrderStatusRepository;
import com.example.pvpeev.electronics_store.order.repository.WarehouseOrderRepository;
import com.example.pvpeev.electronics_store.order.status.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DebitCardPaymentHandler implements PaymentTypeHandler {

    private final OrderStatusRepository orderStatusRepository;
    private final WarehouseOrderRepository warehouseOrderRepository;


    @Override
    public PaymentType getSupportedPaymentType() {
        return PaymentType.DEBIT_CARD;
    }

    @Override
    @Transactional
    public void handlePayment(OrderDetails order) {
        final OrderStatus orderStatus = OrderStatus.valueOf(order.getPreviousOrderStatus());
        if (OrderStatus.ACCEPTED == orderStatus) {
            System.out.println("Taxing user " + order.getUserId() + " amount " + order.getTotalPrice());
            orderStatusRepository.save(new OrderStatusEntity(null, order.getOrderId(), OrderStatus.CONFIRMED.getId()));
            orderStatusRepository.save(new OrderStatusEntity(null, order.getOrderId(), OrderStatus.PAID.getId()));
            warehouseOrderRepository.save(new WarehouseOrderEntity(null, order.getOrderId(), null));
            orderStatusRepository.save(new OrderStatusEntity(null, order.getOrderId(), OrderStatus.WAITING_WAREHOUSE.getId()));
        } else if (OrderStatus.DELIVERED == orderStatus) {
            orderStatusRepository.save(new OrderStatusEntity(null, order.getOrderId(), OrderStatus.COMPLETED.getId()));
        }
    }
}
