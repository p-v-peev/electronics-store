package com.example.pvpeev.electronics_store.product.service;

import com.example.pvpeev.electronics_store.product.dto.ProductBrandRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductBrandResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductBrandEntity;
import com.example.pvpeev.electronics_store.product.mapper.ProductBrandMapper;
import com.example.pvpeev.electronics_store.product.repository.ProductBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductBrandService {

    private final ProductBrandMapper productBrandMapper;

    private final ProductBrandRepository productBrandRepository;


    public List<ProductBrandResponse> getAll() {
        return productBrandRepository.findAll().stream().map(productBrandMapper::toResponse).toList();
    }

    public void create(ProductBrandRequest request) {
        final ProductBrandEntity entity = productBrandMapper.toEntity(request);
        productBrandRepository.save(entity);
    }
}
