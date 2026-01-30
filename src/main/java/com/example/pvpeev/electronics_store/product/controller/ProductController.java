package com.example.pvpeev.electronics_store.product.controller;

import com.example.pvpeev.electronics_store.product.dto.ProductImageRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductImageResponse;
import com.example.pvpeev.electronics_store.product.dto.ProductResponse;
import com.example.pvpeev.electronics_store.product.service.ProductImageService;
import com.example.pvpeev.electronics_store.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.pvpeev.electronics_store.product.controller.ProductController.PATH;

@RestController
@RequestMapping(PATH)
@RequiredArgsConstructor
public class ProductController {

    public static final String PATH = "/api/v1/products";

    private final ProductService productService;

    private final ProductImageService productImageService;

    @GetMapping("/{id}/images")
    public ResponseEntity<List<ProductImageResponse>> getProductImages(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(productImageService.getByProductId(id));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ProductImageResponse> create(@ModelAttribute ProductImageRequest request, @PathVariable("id") UUID id, UriComponentsBuilder ucb) {
        final ProductImageResponse response = productImageService.create(request, id);
        return ResponseEntity.created(ucb.path(PATH).pathSegment("{id}", "images").build(id)).body(response);
    }

    // TODO handle delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByID(@PathVariable("id") Integer id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
