package com.example.pvpeev.electronics_store.product.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Getter
public class ProductRequest {
    private final Integer productBrandId;
    private final String name;
    private final String description;
    private final int price;
    private final int quantityAvailable;
    private final MultipartFile thumbnailImage;
}
