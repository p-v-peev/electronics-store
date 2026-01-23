package com.example.pvpeev.electronics_store.product.repository;

import com.example.pvpeev.electronics_store.product.entity.ProductBrandEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductBrandRepository extends CrudRepository<ProductBrandEntity, UUID>, PagingAndSortingRepository<ProductBrandEntity, UUID> {
}
