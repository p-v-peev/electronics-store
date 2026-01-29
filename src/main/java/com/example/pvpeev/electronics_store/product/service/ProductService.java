package com.example.pvpeev.electronics_store.product.service;

import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.blob.BlobStorageService;
import com.example.pvpeev.electronics_store.product.dto.ProductRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductEntity;
import com.example.pvpeev.electronics_store.product.mapper.ProductMapper;
import com.example.pvpeev.electronics_store.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.example.pvpeev.electronics_store.TextConstants.PRODUCT_THUMBNAILS_STORAGE;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    private final BlobStorageService fileUploadService;


    public Page<ProductResponse> getAll(Integer id, Pageable pageable) {
        final Page<ProductEntity> productEntitiesPage = productRepository.findAllByProductCategoryId(id, pageable);
        return productEntitiesPage.map(productMapper::toResponse);
    }

    public Optional<ProductResponse> getById(UUID id) {
        return productRepository.findById(id).map(productMapper::toResponse);
    }

    public String create(ProductRequest request, UUID categoryId) {
        final String imageUrl = fileUploadService.uploadFile(PRODUCT_THUMBNAILS_STORAGE.getValue(), request.getThumbnailImage());
        final ProductEntity entity = productMapper.toEntity(request, categoryId, imageUrl);
        return productRepository.save(entity).getId().toString();
    }

    public void deleteById(Integer id) {
        final int result = productRepository.deleteByIdWithCount(id);
        if (result == 0) {
            throw new ResourceNotFoundException();
        }
    }
}
