package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.advice.exception.BadRequestException;
import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.order.dto.OrderRequest;
import com.example.pvpeev.electronics_store.order.dto.OrderResponse;
import com.example.pvpeev.electronics_store.order.dto.internal.OrderRequestWithId;
import com.example.pvpeev.electronics_store.order.entity.OrderEntity;
import com.example.pvpeev.electronics_store.order.mapper.OrderMapper;
import com.example.pvpeev.electronics_store.order.mapper.OrderMapperImpl;
import com.example.pvpeev.electronics_store.order.mapper.PaymentTypeMapperImpl;
import com.example.pvpeev.electronics_store.order.mapper.ShippingMethodMapperImpl;
import com.example.pvpeev.electronics_store.order.payment.DebitCardVisaPaymentHandler;
import com.example.pvpeev.electronics_store.order.payment.PaymentType;
import com.example.pvpeev.electronics_store.order.pipeline.OrderPipelineStage;
import com.example.pvpeev.electronics_store.order.repository.OrderRepository;
import com.example.pvpeev.electronics_store.order.shipping.DhlShippingMethodHandler;
import com.example.pvpeev.electronics_store.order.shipping.ShippingMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Spy
    private OrderMapper orderMapper = new OrderMapperImpl();

    @Mock
    private OrderRepository orderRepository;

    @Spy
    private PaymentTypeService paymentTypeService = new PaymentTypeService(new PaymentTypeMapperImpl(), List.of(new DebitCardVisaPaymentHandler(null, null)));

    @Spy
    private ShippingMethodService shippingMethodService = new ShippingMethodService(new ShippingMethodMapperImpl(), List.of(new DhlShippingMethodHandler()));

    @Mock()
    private Supplier<UUID> uuidSupplier;

    @Mock
    private Clock timeSupplier;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void testFindByIdExistingProduct() {
        final OrderEntity orderEntity = new OrderEntity(UUID.randomUUID(), UUID.randomUUID(), "Test address", PaymentType.DEBIT_CARD_VISA.getId(), "0878414040", UUID.randomUUID().toString(), ShippingMethod.DHL.getId());
        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
        assertThat(orderService.findById(orderEntity.getId()))
                .isEqualTo(new OrderResponse(orderEntity.getId(), orderEntity.getUserId(), orderEntity.getOrderAddress(), orderEntity.getPaymentType(), orderEntity.getPhoneNumber(), orderEntity.getTrackingCode(), orderEntity.getShippingMethod()));

        verify(orderRepository, times(1)).findById(orderEntity.getId());
        verify(orderMapper, times(1)).toResponse(orderEntity);
    }

    @Test
    public void testFindByIdUnexistingProduct() {
        final UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        final RuntimeException exception = catchRuntimeException(() -> orderService.findById(orderId));
        assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderMapper, times(0)).toResponse(any(OrderEntity.class));
    }

    @Test
    public void testIngestOrderInvalidPaymentType() {
        final OrderRequest request = new OrderRequest(UUID.randomUUID(), "Test address", "INVALID_PAYMENT", "088314040", ShippingMethod.DHL.getName(), List.of());
        final Exception exception = catchRuntimeException(() -> orderService.ingestOrder(request));

        assertThat(exception)
                .isInstanceOf(BadRequestException.class);

        verify(paymentTypeService, times(1)).getPaymentTypeByName(request.getPaymentType());
        verify(shippingMethodService, times(0)).getShippingMethodByName(any(String.class));
        verify(kafkaTemplate, times(0)).send(eq(OrderPipelineStage.ACCEPTED.getStage()), any(OrderRequestWithId.class));
    }

    @Test
    public void testIngestOrderInvalidShippingMethod() {
        final OrderRequest request = new OrderRequest(UUID.randomUUID(), "Test address", PaymentType.DEBIT_CARD_VISA.getName(), "088314040", "INVALID_SHIPPING_METHOD", List.of());
        final Exception exception = catchRuntimeException(() -> orderService.ingestOrder(request));
        assertThat(exception)
                .isInstanceOf(BadRequestException.class);

        verify(paymentTypeService, times(1)).getPaymentTypeByName(request.getPaymentType());
        verify(shippingMethodService, times(1)).getShippingMethodByName(request.getShippingMethod());
        verify(kafkaTemplate, times(0)).send(eq(OrderPipelineStage.ACCEPTED.getStage()), any(OrderRequestWithId.class));
    }

    @Test
    public void testIngestOrder() {
        final UUID orderId = UUID.randomUUID();
        final Instant orderTime = Instant.now();
        final OrderRequest request = new OrderRequest(UUID.randomUUID(), "Test address", PaymentType.DEBIT_CARD_VISA.getName(), "088314040", ShippingMethod.DHL.getName(), List.of());
        final OrderRequestWithId expectedOrderRequestEithId = new OrderRequestWithId(orderId, request, orderTime);

        when(uuidSupplier.get()).thenReturn(orderId);
        when(timeSupplier.instant()).thenReturn(orderTime);
        when(kafkaTemplate.send(OrderPipelineStage.ACCEPTED.getStage(), expectedOrderRequestEithId))
                .thenReturn(CompletableFuture.completedFuture(null));

        assertThat(orderService.ingestOrder(request))
                .isCompletedWithValue(orderId);

        verify(paymentTypeService, times(1)).getPaymentTypeByName(request.getPaymentType());
        verify(shippingMethodService, times(1)).getShippingMethodByName(request.getShippingMethod());
        verify(uuidSupplier, times(1)).get();
        verify(timeSupplier,times(1)).instant();
        verify(kafkaTemplate, times(1)).send(OrderPipelineStage.ACCEPTED.getStage(), expectedOrderRequestEithId);
    }
}
