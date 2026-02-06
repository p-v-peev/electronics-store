package com.example.pvpeev.electronics_store.product.repository;

import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends ListCrudRepository<ProductCategoryEntity, Integer> {

    Optional<ProductCategoryEntity> findByPath(String path);

}
