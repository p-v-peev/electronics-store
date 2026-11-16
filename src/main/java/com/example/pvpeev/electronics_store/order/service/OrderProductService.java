package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.repository.OrderProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;

    public int getTotalPrice(UUID orderId) {
        return orderProductRepository.getTotalOrderPrice(orderId);
    }
}
