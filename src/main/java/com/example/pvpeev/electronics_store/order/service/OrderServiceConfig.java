package com.example.pvpeev.electronics_store.order.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.UUID;
import java.util.function.Supplier;

@Configuration
public class OrderServiceConfig {

    @Bean
    public Supplier<UUID> uuidSupplier() {
        return UUID::randomUUID;
    }

    @Bean
    public Clock timeSupplier() {
        return Clock.systemUTC();
    }
}
