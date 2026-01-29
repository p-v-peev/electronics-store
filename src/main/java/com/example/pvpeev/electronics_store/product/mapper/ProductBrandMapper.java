package com.example.pvpeev.electronics_store.product.mapper;

import com.example.pvpeev.electronics_store.product.dto.ProductBrandRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductBrandResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductBrandEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProductBrandMapper {

    ProductBrandResponse toResponse(ProductBrandEntity entity);

    @Mapping(target = "id", ignore = true)
    ProductBrandEntity toEntity(ProductBrandRequest request);

}
