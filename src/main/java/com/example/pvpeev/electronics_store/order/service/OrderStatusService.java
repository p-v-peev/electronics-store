package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.status.OrderStatus;
import com.example.pvpeev.electronics_store.order.dto.OrderStatusResponse;
import com.example.pvpeev.electronics_store.order.mapper.OrderStatusMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderStatusMapper orderStatusMapper;

    public List<OrderStatusResponse> getAllOrderStatuses() {
        return Arrays.stream(OrderStatus.values()).map(orderStatusMapper::toResponse).toList();
    }

}
