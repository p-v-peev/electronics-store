package com.example.pvpeev.electronics_store.product.service;

import com.example.pvpeev.electronics_store.advice.exception.BadRequestException;
import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.blob.BlobStorageService;
import com.example.pvpeev.electronics_store.product.dto.ProductRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductImageEntity;
import com.example.pvpeev.electronics_store.product.mapper.ProductMapper;
import com.example.pvpeev.electronics_store.product.repository.ProductCategoryRepository;
import com.example.pvpeev.electronics_store.product.repository.ProductImageRepository;
import com.example.pvpeev.electronics_store.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.pvpeev.electronics_store.TextConstants.PRODUCT_IMAGES_STORAGE;
import static com.example.pvpeev.electronics_store.TextConstants.PRODUCT_THUMBNAILS_STORAGE;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    private final ProductCategoryRepository productCategoryRepository;

    private final ProductImageRepository productImageRepository;

    private final BlobStorageService blobStorageService;


    public Page<ProductResponse> getAll(String path, Pageable pageable) {
        final Optional<ProductCategoryEntity> entity = productCategoryRepository.findByPath(path);
        if (entity.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        final Page<ProductEntity> productEntitiesPage = productRepository.findAllByProductCategoryIdAndDeletedIsFalse(entity.get().getId(), pageable);
        return productEntitiesPage.map(productMapper::toResponse);
    }

    public ProductResponse create(ProductRequest request, MultipartFile image, String path) {
        final Optional<ProductCategoryEntity> entity = productCategoryRepository.findByPath(path);
        if (entity.isEmpty()) {
            throw new BadRequestException();
        }
        final UUID key = UUID.randomUUID();
        final String imageUrl = blobStorageService.getFileUrl(PRODUCT_THUMBNAILS_STORAGE.getValue(), key);
        final ProductEntity savedEntity = productRepository.save(productMapper.toEntity(request, entity.get().getId(), imageUrl, false));

        try {
            blobStorageService.uploadFile(PRODUCT_THUMBNAILS_STORAGE.getValue(), key, image);
        } catch (Exception e) {
            productRepository.deleteById(savedEntity.getId());
            throw e;
        }
        return productMapper.toResponse(savedEntity);
    }

    public void softDeleteById(UUID productId) {
        // Delete the product images from the blob storage
        final List<ProductImageEntity> productImages = productImageRepository.findAllByProductId(productId);
        productImages
                .stream()
                .map(ProductImageEntity::getImageUrl)
                .map(url -> url.substring(url.lastIndexOf('/') + 1))
                .forEach(key -> blobStorageService.delete(PRODUCT_IMAGES_STORAGE.getValue(), key));

        // Delete the product image records in the database
        final List<UUID> productImageIds = productImages.stream().map(ProductImageEntity::getId).toList();
        productImageRepository.deleteAllById(productImageIds);

        final int result = productRepository.softDeleteById(productId);
        if (result == 0) {
            throw new ResourceNotFoundException();
        }
    }

    public Map<UUID, Integer> getProductPrices(Set<UUID> productIds) {
        return productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(ProductEntity::getId, ProductEntity::getPrice));
    }

    public Map<UUID, Integer> getProductQuantities(Set<UUID> productIds) {
        return productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(ProductEntity::getId, ProductEntity::getQuantityAvailable));
    }
}
