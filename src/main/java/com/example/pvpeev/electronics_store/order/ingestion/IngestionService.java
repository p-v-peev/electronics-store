package com.example.pvpeev.electronics_store.order.ingestion;

import com.example.pvpeev.electronics_store.order.dto.OrderRequest;

public interface IngestionService {
    void ingestOrder(OrderRequest request);
}
