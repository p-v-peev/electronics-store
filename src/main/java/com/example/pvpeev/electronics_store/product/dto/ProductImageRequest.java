package com.example.pvpeev.electronics_store.product.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Getter
public class ProductImageRequest {
    private final MultipartFile image;
}
