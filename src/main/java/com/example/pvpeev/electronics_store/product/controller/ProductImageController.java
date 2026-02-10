package com.example.pvpeev.electronics_store.product.controller;

import com.example.pvpeev.electronics_store.product.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.example.pvpeev.electronics_store.product.controller.ProductImageController.PATH;

@RestController
@RequestMapping(PATH)
@RequiredArgsConstructor
public class ProductImageController {

    public static final String PATH = "/api/v1/images";

    private final ProductImageService productImageService;

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> delete(@PathVariable("imageId") UUID imageId) {
        productImageService.delete(imageId);
        return ResponseEntity.noContent().build();
    }
}
