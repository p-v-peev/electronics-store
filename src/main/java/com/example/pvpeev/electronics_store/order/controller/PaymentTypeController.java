package com.example.pvpeev.electronics_store.order.controller;

import com.example.pvpeev.electronics_store.order.dto.PaymentTypeResponse;
import com.example.pvpeev.electronics_store.order.service.PaymentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.pvpeev.electronics_store.order.controller.PaymentTypeController.PATH;

@RestController
@RequestMapping(PATH)
@RequiredArgsConstructor
public class PaymentTypeController {
    public static final String PATH = "/api/v1/payment-types";

    private final PaymentTypeService paymentTypeService;

    @GetMapping
    public ResponseEntity<List<PaymentTypeResponse>> getAll() {
        return ResponseEntity.ok(paymentTypeService.getAllPaymentTypes());
    }
}
