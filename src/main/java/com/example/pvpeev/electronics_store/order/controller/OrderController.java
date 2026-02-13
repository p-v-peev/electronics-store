package com.example.pvpeev.electronics_store.order.controller;

import com.example.pvpeev.electronics_store.order.dto.OrderRequest;
import com.example.pvpeev.electronics_store.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.pvpeev.electronics_store.order.controller.OrderController.PATH;

@RestController
@RequestMapping(PATH)
@RequiredArgsConstructor
public class OrderController {
    public static final String PATH = "/api/v1/orders";

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> placeOrder(@RequestBody OrderRequest orderRequest) {
        orderService.placeOrder(orderRequest);
        return ResponseEntity.accepted().build();
    }
}
