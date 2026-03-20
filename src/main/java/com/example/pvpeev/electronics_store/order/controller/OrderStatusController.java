package com.example.pvpeev.electronics_store.order.controller;

import com.example.pvpeev.electronics_store.order.dto.OrderStatusResponse;
import com.example.pvpeev.electronics_store.order.service.OrderStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.pvpeev.electronics_store.order.controller.OrderStatusController.PATH;

@RestController
@RequestMapping(PATH)
@RequiredArgsConstructor
public class OrderStatusController {
    public static final String PATH = "/api/v1/order-statuses";

    private final OrderStatusService orderStatusService;

    @GetMapping
    public ResponseEntity<List<OrderStatusResponse>> getAll() {
        return ResponseEntity.ok(orderStatusService.getAllStatuses());
    }
}
