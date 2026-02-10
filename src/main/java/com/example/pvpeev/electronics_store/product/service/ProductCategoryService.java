package com.example.pvpeev.electronics_store.product.service;

import com.example.pvpeev.electronics_store.product.dto.ProductCategoryRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductCategoryResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import com.example.pvpeev.electronics_store.product.mapper.ProductCategoryMapper;
import com.example.pvpeev.electronics_store.product.repository.ProductCategoryRepository;
import com.example.pvpeev.electronics_store.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryMapper productCategoryMapper;

    private final ProductCategoryRepository productCategoryRepository;

    public List<ProductCategoryResponse> findAll() {
        return productCategoryRepository.findAll().stream().map(productCategoryMapper::toResponse).toList();
    }

    public ProductCategoryResponse create(ProductCategoryRequest request) {
        final ProductCategoryEntity entity = productCategoryMapper.toEntity(request);
        final ProductCategoryEntity save = productCategoryRepository.save(entity);
        return productCategoryMapper.toResponse(save);
    }

    public void deleteById(Integer id) {
        productCategoryRepository.deleteById(id);
    }
}
