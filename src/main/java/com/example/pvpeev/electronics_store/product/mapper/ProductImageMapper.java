package com.example.pvpeev.electronics_store.product.mapper;

import com.example.pvpeev.electronics_store.product.dto.ProductImageResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public interface ProductImageMapper {

    ProductImageResponse toResponse(ProductImageEntity entity);

    @Mapping(target = "id", ignore = true)
    ProductImageEntity toEntity(UUID productId, String imageUrl);

}
