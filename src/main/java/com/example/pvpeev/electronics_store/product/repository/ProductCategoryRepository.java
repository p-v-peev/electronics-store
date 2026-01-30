package com.example.pvpeev.electronics_store.product.repository;

import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductCategoryRepository extends ListCrudRepository<ProductCategoryEntity, Integer> {

    @Modifying
    @Query("DELETE FROM user_address WHERE id = :id")
    int deleteByIdWithCount(Integer id);

    Optional<ProductCategoryEntity> findByPath(String path);

}
