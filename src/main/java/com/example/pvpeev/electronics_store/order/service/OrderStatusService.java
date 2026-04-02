package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.dto.OrderStatusResponse;
import com.example.pvpeev.electronics_store.order.entity.OrderStatusEntity;
import com.example.pvpeev.electronics_store.order.mapper.OrderStatusMapper;
import com.example.pvpeev.electronics_store.order.repository.OrderStatusRepository;
import com.example.pvpeev.electronics_store.order.status.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderStatusService {

    private final OrderStatusMapper orderStatusMapper;
    private final OrderStatusRepository orderStatusRepository;
    private final Map<Integer, OrderStatus> orderStatusMap;
    private final Map<String, OrderStatus> orderStatusesByName;

    public OrderStatusService(OrderStatusMapper orderStatusMapper, OrderStatusRepository orderStatusRepository) {
        this.orderStatusMapper = orderStatusMapper;
        this.orderStatusRepository = orderStatusRepository;
        this.orderStatusMap = Arrays.stream(OrderStatus.values()).collect(Collectors.toMap(OrderStatus::getId, Function.identity()));
        this.orderStatusesByName = Arrays.stream(OrderStatus.values()).collect(Collectors.toMap(OrderStatus::name, Function.identity()));
    }

    public List<OrderStatusResponse> getAllStatuses() {
        return Arrays.stream(OrderStatus.values()).map(orderStatusMapper::toResponse).toList();
    }

    public List<OrderStatusResponse> getAllStatusesForOrder(UUID orderId) {
        return orderStatusRepository.findAllByOrderId(orderId).stream()
                .map(OrderStatusEntity::getOrderStatus)
                .map(orderStatusMap::get)
                .map(orderStatusMapper::toResponse)
                .toList();
    }

    public Optional<OrderStatus> getOrderStatusByName(String name) {
        return Optional.ofNullable(orderStatusesByName.get(name));
    }

}
