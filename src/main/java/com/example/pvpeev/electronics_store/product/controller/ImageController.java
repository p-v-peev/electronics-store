package com.example.pvpeev.electronics_store.product.controller;

import com.example.pvpeev.electronics_store.product.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.example.pvpeev.electronics_store.product.controller.ImageController.PATH;

@RestController
@RequestMapping(PATH)
@RequiredArgsConstructor
public class ImageController {

    public static final String PATH = "/api/v1/images";

    private final ProductImageService productImageService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        productImageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
