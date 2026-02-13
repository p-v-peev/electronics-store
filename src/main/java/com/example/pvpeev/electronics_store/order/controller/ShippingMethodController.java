package com.example.pvpeev.electronics_store.order.controller;

import com.example.pvpeev.electronics_store.order.dto.ShippingMethodResponse;
import com.example.pvpeev.electronics_store.order.service.ShippingMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.pvpeev.electronics_store.order.controller.ShippingMethodController.PATH;

@RestController
@RequestMapping(PATH)
@RequiredArgsConstructor
public class ShippingMethodController {
    public static final String PATH = "/api/v1/shipping-methods";

    private final ShippingMethodService shippingMethodService;

    @GetMapping
    public ResponseEntity<List<ShippingMethodResponse>> getAll() {
        return ResponseEntity.ok(shippingMethodService.getAllShippingMethods());
    }
}
