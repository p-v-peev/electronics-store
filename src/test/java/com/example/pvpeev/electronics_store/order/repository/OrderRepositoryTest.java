package com.example.pvpeev.electronics_store.order.repository;

import com.example.pvpeev.electronics_store.auth.entity.UserEntity;
import com.example.pvpeev.electronics_store.auth.repository.UserRepository;
import com.example.pvpeev.electronics_store.order.entity.OrderEntity;
import com.example.pvpeev.electronics_store.order.payment.PaymentType;
import com.example.pvpeev.electronics_store.order.shipping.ShippingMethod;
import com.example.pvpeev.electronics_store.repository.BaseRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.UUID;

import static com.example.pvpeev.electronics_store.order.payment.PaymentType.DEBIT_CARD;
import static com.example.pvpeev.electronics_store.order.shipping.ShippingMethod.DHL;
import static org.assertj.core.api.Assertions.*;

public class OrderRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testAddingOrderForUnexistingUserThrowsException() {
        final OrderEntity orderToSave = new OrderEntity(UUID.randomUUID(), UUID.randomUUID(), "Test address", DEBIT_CARD.getId(), "00000000", null, DHL.getId()).setNew();
        final RuntimeException exception = catchRuntimeException(() -> orderRepository.save(orderToSave));
        assertThat(exception)
                .as("Orders can't be placed by unexisting users")
                .isInstanceOf(DataIntegrityViolationException.class)
                .extracting(Throwable::getMessage)
                .matches(s -> s.contains("fk_user_id"));
    }

    @Test
    public void testSetOrderTrackingCode() {
        final UserEntity savedUser = newUser();
        final UUID orderId = UUID.randomUUID();
        final OrderEntity orderToSave = new OrderEntity(orderId, savedUser.getId(), "Test address", DEBIT_CARD.getId(), "00000000", null, DHL.getId()).setNew();
        final OrderEntity savedOrder = orderRepository.save(orderToSave);
        assertThat(savedOrder.getTrackingCode())
                .as("New orders must not have tracking code")
                .isNull();
        final UUID trackingCode = UUID.randomUUID();
        orderRepository.setOrderTrackingCode(orderId, trackingCode);
        assertThat(orderRepository.findById(orderId))
                .isPresent()
                .get()
                .extracting(OrderEntity::getTrackingCode)
                .as("The tracking code must be equal to the one set")
                .isEqualTo(trackingCode);
    }

    @Test
    public void testUsingUnexistingShippingMethodThrowsException() {
        final UserEntity savedUser = newUser();
        final OrderEntity orderToSave = new OrderEntity(UUID.randomUUID(), savedUser.getId(), "Test address", DEBIT_CARD.getId(), "00000000", null, -1).setNew();
        final RuntimeException exception = catchRuntimeException(() -> orderRepository.save(orderToSave));
        assertThat(exception)
                .as("Unsupported shipping methods can't be used")
                .isInstanceOf(DataIntegrityViolationException.class)
                .extracting(Throwable::getMessage)
                .matches(s -> s.contains("check_shipping_method"));
    }

    @Test
    public void testUsingAllShippingMethodsIsPossible() {
        final UserEntity savedUser = newUser();
        Arrays.stream(ShippingMethod.values())
                .forEach(shippingMethod -> {
                    final OrderEntity orderToSave = new OrderEntity(UUID.randomUUID(), savedUser.getId(), "Test address", DEBIT_CARD.getId(), "00000000", null, shippingMethod.getId()).setNew();
                    assertThatNoException().isThrownBy(() -> orderRepository.save(orderToSave));
                });
    }

    @Test
    public void testUsingUnexistingPaymentMethodThrowsException() {
        final UserEntity savedUser = newUser();
        final OrderEntity orderToSave = new OrderEntity(UUID.randomUUID(), savedUser.getId(), "Test address", -1, "00000000", null, DHL.getId()).setNew();
        final RuntimeException exception = catchRuntimeException(() -> orderRepository.save(orderToSave));
        assertThat(exception)
                .as("Unsupported payment types can't be used")
                .isInstanceOf(DataIntegrityViolationException.class)
                .extracting(Throwable::getMessage)
                .matches(s -> s.contains("check_payment_type"));
    }

    @Test
    public void testUsingAllPaymentTypesIsPossible() {
        final UserEntity savedUser = newUser();
        Arrays.stream(PaymentType.values())
                .forEach(paymentType -> {
                    final OrderEntity orderToSave = new OrderEntity(UUID.randomUUID(), savedUser.getId(), "Test address", paymentType.getId(), "00000000", null, DHL.getId()).setNew();
                    assertThatNoException().isThrownBy(() -> orderRepository.save(orderToSave));
                });
    }

    private UserEntity newUser() {
        return userRepository.save(new UserEntity(null, "pvpeev@store.com", "Plamen", "Peev", "{noop}password", "+359897401213", true));
    }
}
