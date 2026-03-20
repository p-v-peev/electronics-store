package com.example.pvpeev.electronics_store.order.controller;

import com.example.pvpeev.electronics_store.order.dto.OrderRequest;
import com.example.pvpeev.electronics_store.order.dto.OrderResponse;
import com.example.pvpeev.electronics_store.order.dto.OrderStatusResponse;
import com.example.pvpeev.electronics_store.order.dto.ShipmentStatusUpdate;
import com.example.pvpeev.electronics_store.order.service.OrderService;
import com.example.pvpeev.electronics_store.order.service.OrderStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.example.pvpeev.electronics_store.order.controller.OrderController.PATH;

@RestController
@RequestMapping(PATH)
@RequiredArgsConstructor
public class OrderController {
    public static final String PATH = "/api/v1/orders";

    private final OrderService orderService;
    private final OrderStatusService orderStatusService;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getById(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.ok(orderService.findById(orderId));
    }

    @GetMapping("/{orderId}/statuses")
    public ResponseEntity<List<OrderStatusResponse>> getStatusesByOrderId(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.ok(orderStatusService.getAllStatusesForOrder(orderId));
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<Void>> placeOrder(@RequestBody OrderRequest orderRequest, UriComponentsBuilder ucb) {
        return orderService.ingestOrder(orderRequest)
                .thenApply(id -> {
                    final URI uri = ucb.path(PATH).pathSegment("{id}").build(id);
                    return ResponseEntity.accepted().location(uri).build();
                });
    }

    @PatchMapping("/{orderId}/confirmations")
    public ResponseEntity<Void> confirmOrder(@PathVariable("orderId") UUID orderId) {
        orderService.confirmOrder(orderId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{orderId}/shipments")
    public ResponseEntity<Void> arrangeShipping(@PathVariable("orderId") UUID orderId) {
        orderService.arrangeShipping(orderId);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{orderId}/shipments")
    public ResponseEntity<Void> updateStatus(@PathVariable("orderId") UUID orderId, @RequestBody ShipmentStatusUpdate update) throws ExecutionException, InterruptedException {
        orderService.updateStatus(orderId, update);
        return ResponseEntity.accepted().build();
    }
}
