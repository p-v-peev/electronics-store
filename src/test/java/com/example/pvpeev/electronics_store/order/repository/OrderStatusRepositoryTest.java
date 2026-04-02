package com.example.pvpeev.electronics_store.order.repository;

import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import com.example.pvpeev.electronics_store.order.entity.OrderEntity;
import com.example.pvpeev.electronics_store.order.entity.OrderStatusEntity;
import com.example.pvpeev.electronics_store.order.status.OrderStatus;
import com.example.pvpeev.electronics_store.repository.BaseRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import static com.example.pvpeev.electronics_store.order.payment.PaymentType.DEBIT_CARD_VISA;
import static com.example.pvpeev.electronics_store.order.shipping.ShippingMethod.DHL;
import static org.assertj.core.api.Assertions.*;

public class OrderStatusRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testAddingStatusForUnexistingOrderThrowsException() {
        final RuntimeException exception = catchRuntimeException(() -> orderStatusRepository.save(new OrderStatusEntity(null, UUID.randomUUID(), OrderStatus.ACCEPTED.getId())));
        assertThat(exception)
                .as("Statuses can't be placed for unexisting orders")
                .isInstanceOf(DataIntegrityViolationException.class)
                .extracting(Throwable::getMessage)
                .matches(s -> s.contains("fk_order_id"));
    }

    @Test
    public void testAddingUnexistingStatusThrowsException() {
        final UserEntity savedUser = newUser();
        final OrderEntity orderToSave = new OrderEntity(UUID.randomUUID(), savedUser.getId(), "Test address", DEBIT_CARD_VISA.getId(), "00000000", null, DHL.getId()).setNew();
        final UUID orderId = orderRepository.save(orderToSave).getId();

        final RuntimeException exception = catchRuntimeException(() -> orderStatusRepository.save(new OrderStatusEntity(null, orderId, -1)));
        assertThat(exception)
                .as("Unsupported statuses can't be used")
                .isInstanceOf(DataIntegrityViolationException.class)
                .extracting(Throwable::getMessage)
                .matches(s -> s.contains("check_order_status"));
    }

    @Test
    public void testAddingAllExistingStatusesIsPossible() {
        final UserEntity savedUser = newUser();
        final OrderEntity orderToSave = new OrderEntity(UUID.randomUUID(), savedUser.getId(), "Test address", DEBIT_CARD_VISA.getId(), "00000000", null, DHL.getId()).setNew();
        final UUID orderId = orderRepository.save(orderToSave).getId();
        Arrays.stream(OrderStatus.values())
                .forEach(orderStatus -> {
                    assertThatNoException().isThrownBy(() -> orderStatusRepository.save(new OrderStatusEntity(null, orderId, orderStatus.getId())));
                });
    }

    @Test
    public void testGetAllStatusesForOrder() {
        final UserEntity savedUser = newUser();
        final OrderEntity orderToSave = new OrderEntity(UUID.randomUUID(), savedUser.getId(), "Test address", DEBIT_CARD_VISA.getId(), "00000000", null, DHL.getId()).setNew();
        final UUID orderId = orderRepository.save(orderToSave).getId();
        Stream.of(OrderStatus.ACCEPTED, OrderStatus.CONFIRMED)
                .forEach(orderStatus -> orderStatusRepository.save(new OrderStatusEntity(null, orderId, orderStatus.getId())));

        assertThat(orderStatusRepository.findAllByOrderId(orderId))
                .as("The order must have only the ACCEPTED and CONFIRMED")
                .extracting(OrderStatusEntity::getOrderStatus)
                .containsExactlyInAnyOrder(
                        OrderStatus.ACCEPTED.getId(),
                        OrderStatus.CONFIRMED.getId()
                );
    }

    private UserEntity newUser() {
        return userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true));
    }
}

