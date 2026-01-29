package com.example.pvpeev.electronics_store.product.mapper;

import com.example.pvpeev.electronics_store.product.dto.ProductRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public interface ProductMapper {

    ProductResponse toResponse(ProductEntity entity);

    @Mapping(target = "id", ignore = true)
    ProductEntity toEntity(ProductRequest request, UUID productCategoryId, String thumbnailImageUrl);

}
