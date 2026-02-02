package com.example.pvpeev.electronics_store.product.service;

import com.example.pvpeev.electronics_store.advice.exception.BadRequestException;
import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.blob.BlobStorageService;
import com.example.pvpeev.electronics_store.product.dto.ProductRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductEntity;
import com.example.pvpeev.electronics_store.product.mapper.ProductMapper;
import com.example.pvpeev.electronics_store.product.repository.ProductCategoryRepository;
import com.example.pvpeev.electronics_store.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.pvpeev.electronics_store.TextConstants.PRODUCT_THUMBNAILS_STORAGE;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    private final ProductCategoryRepository productCategoryRepository;

    private final BlobStorageService blobStorageService;


    public Page<ProductResponse> getAll(String path, Pageable pageable) {
        final Optional<ProductCategoryEntity> entity = productCategoryRepository.findByPath(path);
        if (entity.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        final Page<ProductEntity> productEntitiesPage = productRepository.findAllByProductCategoryId(entity.get().getId(), pageable);
        return productEntitiesPage.map(productMapper::toResponse);
    }

    public void create(ProductRequest request, String path) {
        final Optional<ProductCategoryEntity> entity = productCategoryRepository.findByPath(path);
        if (entity.isEmpty()) {
            throw new BadRequestException();
        }
        final String imageUrl = blobStorageService.uploadFile(PRODUCT_THUMBNAILS_STORAGE.getValue(), request.getThumbnailImage());

        try {
            final ProductEntity productEntity = productMapper.toEntity(request, entity.get().getId(), imageUrl);
            productRepository.save(productEntity);
        } catch (Exception e) {
            blobStorageService.deleteFile(PRODUCT_THUMBNAILS_STORAGE.getValue(), imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
            throw e;
        }
    }

    public void deleteById(Integer id) {
        final int result = productRepository.deleteByIdWithCount(id);
        if (result == 0) {
            throw new ResourceNotFoundException();
        }
    }
}
