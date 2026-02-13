package com.example.pvpeev.electronics_store.order.ingestion.redis;

import com.example.pvpeev.electronics_store.order.dto.OrderRequest;
import com.example.pvpeev.electronics_store.order.ingestion.IngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisOrderIngestionService implements IngestionService {

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;

    private static final String STREAM_KEY = "orders:stream";
    private static final String DATA_KEY = "data";

    @Override
    public void ingestOrder(OrderRequest request) {
        stringRedisTemplate.opsForStream().add(STREAM_KEY, Map.of(DATA_KEY, objectMapper.writeValueAsString(request)));
    }
}
