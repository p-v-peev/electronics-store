package com.example.pvpeev.electronics_store.product.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class ProductRequest {
    private final UUID productBrandId;
    private final String name;
    private final String description;
    private final int price;
    private final int quantityAvailable;
    private final MultipartFile thumbnailImage;
}
