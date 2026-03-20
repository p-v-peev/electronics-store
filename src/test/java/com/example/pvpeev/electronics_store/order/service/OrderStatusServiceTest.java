package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.dto.OrderStatusResponse;
import com.example.pvpeev.electronics_store.order.entity.OrderStatusEntity;
import com.example.pvpeev.electronics_store.order.mapper.OrderStatusMapper;
import com.example.pvpeev.electronics_store.order.mapper.OrderStatusMapperImpl;
import com.example.pvpeev.electronics_store.order.repository.OrderStatusRepository;
import com.example.pvpeev.electronics_store.order.status.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderStatusServiceTest {

    @Spy
    private OrderStatusMapper orderStatusMapper = new OrderStatusMapperImpl();

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @InjectMocks
    private OrderStatusService orderStatusService;

    @Test
    public void testGetOrderStatuses() {

        assertThat(orderStatusService.getAllStatuses())
                .containsExactlyInAnyOrder(
                        new OrderStatusResponse(100, "Order accepted"),
                        new OrderStatusResponse(200, "Waiting confirmation"),
                        new OrderStatusResponse(300, "Order confirmed"),
                        new OrderStatusResponse(400, "Waiting warehouse"),
                        new OrderStatusResponse(500, "Ready for shipping"),
                        new OrderStatusResponse(600, "Waiting for pickup"),
                        new OrderStatusResponse(700, "Order shipped"),
                        new OrderStatusResponse(800, "Out for delivery"),
                        new OrderStatusResponse(900, "Order delivered"),
                        new OrderStatusResponse(1000, "Waiting payment"),
                        new OrderStatusResponse(1100, "Order paid"),
                        new OrderStatusResponse(1200, "Order completed"),
                        new OrderStatusResponse(1300, "Requires human intervention")
                );
    }

    @Test
    public void testGetAllStatusForExistingOrder() {
        final UUID orderId = UUID.randomUUID();
        when(orderStatusRepository.findAllByOrderId(orderId)).thenReturn(List.of(new OrderStatusEntity(1L, orderId, OrderStatus.ACCEPTED.getId())));

        assertThat(orderStatusService.getAllStatusesForOrder(orderId))
                .containsExactly(new OrderStatusResponse(100, "Order accepted"));

        verify(orderStatusRepository, times(1)).findAllByOrderId(orderId);
        verify(orderStatusMapper, times(1)).toResponse(OrderStatus.ACCEPTED);
    }
}
