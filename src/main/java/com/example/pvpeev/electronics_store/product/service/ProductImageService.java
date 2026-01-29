package com.example.pvpeev.electronics_store.product.service;

import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.blob.BlobStorageService;
import com.example.pvpeev.electronics_store.product.dto.ProductImageRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductImageResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductImageEntity;
import com.example.pvpeev.electronics_store.product.mapper.ProductImageMapper;
import com.example.pvpeev.electronics_store.product.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.example.pvpeev.electronics_store.TextConstants.PRODUCT_IMAGES_STORAGE;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageMapper productImageMapper;

    private final ProductImageRepository productImageRepository;

    private final BlobStorageService fileUploadService;

    public List<ProductImageResponse> getByProductId(UUID id) {
        return productImageRepository.findAllByProductId(id).stream().map(productImageMapper::toResponse).toList();
    }

    public ProductImageResponse create(ProductImageRequest request, UUID productId) {
        final String imageUrl = fileUploadService.uploadFile(PRODUCT_IMAGES_STORAGE.getValue(), request.getImage());
        final ProductImageEntity entity = productImageMapper.toEntity(request, productId, imageUrl);
        final ProductImageEntity save = productImageRepository.save(entity);
        return productImageMapper.toResponse(save);
    }

    public void delete(UUID id) {
        int result = productImageRepository.deleteByIdWithCount(id);
        if (result == 0) {
            throw new ResourceNotFoundException();
        }
    }
}
