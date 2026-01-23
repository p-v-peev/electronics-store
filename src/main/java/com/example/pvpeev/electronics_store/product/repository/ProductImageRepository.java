package com.example.pvpeev.electronics_store.product.repository;

import com.example.pvpeev.electronics_store.product.entity.ProductImageEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductImageRepository extends CrudRepository<ProductImageEntity, UUID> {
}
