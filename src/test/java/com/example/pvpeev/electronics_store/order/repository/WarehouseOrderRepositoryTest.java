package com.example.pvpeev.electronics_store.order.repository;

import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import com.example.pvpeev.electronics_store.order.entity.OrderEntity;
import com.example.pvpeev.electronics_store.order.entity.WarehouseBinEntity;
import com.example.pvpeev.electronics_store.order.entity.WarehouseOrderEntity;
import com.example.pvpeev.electronics_store.order.payment.PaymentType;
import com.example.pvpeev.electronics_store.repository.BaseRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static com.example.pvpeev.electronics_store.order.shipping.ShippingMethod.DHL;
import static org.assertj.core.api.Assertions.*;

public class WarehouseOrderRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private WarehouseBinRepository warehouseBinRepository;

    @Autowired
    private WarehouseOrderRepository warehouseOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testAddingToQueueForUnexistingOrderThrowsException() {
        final Integer binId = warehouseBinRepository.save(new WarehouseBinEntity(null, "A-12-3")).getId();

        final RuntimeException exception = catchRuntimeException(() -> warehouseOrderRepository.save(new WarehouseOrderEntity(null, UUID.randomUUID(), binId)));

        assertThat(exception)
                .as("Warehouse queue must contain only valid orders")
                .isInstanceOf(DataIntegrityViolationException.class)
                .extracting(Throwable::getMessage)
                .matches(s -> s.contains("fk_warehouse_order_id"));
    }

    @Test
    public void testAddingToQueueForUnexistingBinThrowsException() {
        final UUID userId = userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true)).getId();
        final UUID orderId = orderRepository.save(new OrderEntity(UUID.randomUUID(), userId, "Test address", PaymentType.DEBIT_CARD.getId(), "00000000", null, DHL.getId()).setNew()).getId();

        final RuntimeException exception = catchRuntimeException(() -> warehouseOrderRepository.save(new WarehouseOrderEntity(null, orderId, -1)));

        assertThat(exception)
                .as("Warehouse queue must contain only items with valid bins")
                .isInstanceOf(DataIntegrityViolationException.class)
                .extracting(Throwable::getMessage)
                .matches(s -> s.contains("fk_warehouse_bin_id"));
    }

    @Test
    public void testAddingWarehouseQueueEntry() {
        final Integer binId = warehouseBinRepository.save(new WarehouseBinEntity(null, "A-12-3")).getId();
        final UUID userId = userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true)).getId();
        final UUID orderId = orderRepository.save(new OrderEntity(UUID.randomUUID(), userId, "Test address", PaymentType.DEBIT_CARD.getId(), "00000000", null, DHL.getId()).setNew()).getId();

        assertThatNoException().isThrownBy(() -> warehouseOrderRepository.save(new WarehouseOrderEntity(null, orderId, binId)));
    }

    @Test
    public void testDeleteByOrderId() {
        final Integer binId = warehouseBinRepository.save(new WarehouseBinEntity(null, "A-12-3")).getId();
        final UUID userId = userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true)).getId();
        final UUID orderId = orderRepository.save(new OrderEntity(UUID.randomUUID(), userId, "Test address", PaymentType.DEBIT_CARD.getId(), "00000000", null, DHL.getId()).setNew()).getId();

        final Long id = warehouseOrderRepository.save(new WarehouseOrderEntity(null, orderId, binId)).getId();

        warehouseOrderRepository.deleteByOrderId(orderId);

        assertThat(warehouseOrderRepository.findById(id))
                .as("The entity must not exist at this point")
                .isEmpty();
    }
}
