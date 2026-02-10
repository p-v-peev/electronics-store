package com.example.pvpeev.electronics_store.product.controller;

import com.example.pvpeev.electronics_store.product.dto.ProductImageResponse;
import com.example.pvpeev.electronics_store.product.service.ProductImageService;
import com.example.pvpeev.electronics_store.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import static com.example.pvpeev.electronics_store.product.controller.ProductController.PATH;

@RestController
@RequestMapping(PATH)
@RequiredArgsConstructor
public class ProductController {

    public static final String PATH = "/api/v1/products";

    private final ProductService productService;

    private final ProductImageService productImageService;

    @GetMapping("/{productId}/images")
    public ResponseEntity<List<ProductImageResponse>> getProductImages(@PathVariable("productId") UUID productId) {
        return ResponseEntity.ok(productImageService.getByProductId(productId));
    }

    @PostMapping("/{productId}/images")
    public ResponseEntity<ProductImageResponse> create(@RequestPart("image") MultipartFile image, @PathVariable("productId") UUID productId, UriComponentsBuilder ucb) {
        final ProductImageResponse response = productImageService.create(image, productId);
        return ResponseEntity.created(ucb.path(PATH).pathSegment("{productId}", "images").build(productId)).body(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteByID(@PathVariable("productId") UUID productId) {
        productService.softDeleteById(productId);
        return ResponseEntity.noContent().build();
    }
}
