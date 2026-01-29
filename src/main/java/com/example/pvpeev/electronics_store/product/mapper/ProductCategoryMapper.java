package com.example.pvpeev.electronics_store.product.mapper;

import com.example.pvpeev.electronics_store.product.dto.ProductCategoryRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductCategoryResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProductCategoryMapper {

    ProductCategoryResponse toResponse(ProductCategoryEntity entity);

    @Mapping(target = "id", ignore = true)
    ProductCategoryEntity toEntity(ProductCategoryRequest request);

}
