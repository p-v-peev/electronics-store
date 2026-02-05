package com.example.pvpeev.electronics_store.product.service;

import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.blob.BlobStorageService;
import com.example.pvpeev.electronics_store.product.dto.ProductImageRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductImageResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductImageEntity;
import com.example.pvpeev.electronics_store.product.mapper.ProductImageMapper;
import com.example.pvpeev.electronics_store.product.repository.ProductImageRepository;
import com.example.pvpeev.electronics_store.product.repository.ProductRepository;
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

    private final ProductRepository productRepository;

    private final BlobStorageService blobStorageService;

    public List<ProductImageResponse> getByProductId(UUID id) {
        final boolean exists = productRepository.existsById(id);
        if (!exists) {
            throw new ResourceNotFoundException();
        }
        return productImageRepository.findAllByProductId(id).stream().map(productImageMapper::toResponse).toList();
    }

    public ProductImageResponse create(ProductImageRequest imageRequest, UUID productId) {
        final UUID key = UUID.randomUUID();
        final String imageUrl = blobStorageService.getFileUrl(PRODUCT_IMAGES_STORAGE.getValue(), key);
        final ProductImageEntity savedEntity = productImageRepository.save(productImageMapper.toEntity(imageRequest, productId, imageUrl));
        try {
            blobStorageService.uploadFile(PRODUCT_IMAGES_STORAGE.getValue(), key, imageRequest.getImage());
        } catch (Exception e) {
            productImageRepository.deleteById(savedEntity.getId());
            throw e;
        }
        return productImageMapper.toResponse(savedEntity);
    }

    public void delete(UUID imageId) {
        productImageRepository.findById(imageId)
                .map(ProductImageEntity::getImageUrl)
                .map(url -> url.substring(url.lastIndexOf('/') + 1))
                .ifPresent(key -> blobStorageService.delete(PRODUCT_IMAGES_STORAGE.getValue(), key));

        int result = productImageRepository.deleteByIdWithCount(imageId);
        if (result == 0) {
            throw new ResourceNotFoundException();
        }
    }
}
