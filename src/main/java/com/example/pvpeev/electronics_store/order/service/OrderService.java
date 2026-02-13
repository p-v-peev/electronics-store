package com.example.pvpeev.electronics_store.order.service;

import com.example.pvpeev.electronics_store.order.dto.OrderRequest;
import com.example.pvpeev.electronics_store.order.ingestion.IngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final IngestionService ingestionService;

    public void placeOrder(OrderRequest orderRequest) {
        // Send the order to redis stream;
        ingestionService.ingestOrder(orderRequest);
    }
}
