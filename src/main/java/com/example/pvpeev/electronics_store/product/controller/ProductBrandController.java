package com.example.pvpeev.electronics_store.product.controller;

import com.example.pvpeev.electronics_store.product.dto.ProductBrandRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductBrandResponse;
import com.example.pvpeev.electronics_store.product.service.ProductBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.example.pvpeev.electronics_store.product.controller.ProductBrandController.PATH;

@RestController
@RequestMapping(PATH)
@RequiredArgsConstructor
public class ProductBrandController {

    public static final String PATH = "/api/v1/brands";

    private final ProductBrandService productBrandService;


    @GetMapping
    public ResponseEntity<List<ProductBrandResponse>> getAll() {
        return ResponseEntity.ok(productBrandService.getAll());
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody ProductBrandRequest request, UriComponentsBuilder ucb) {
        final String s = productBrandService.create(request);
        return ResponseEntity.created(ucb.path(PATH).pathSegment("{id}").build(s)).build();
    }
}
