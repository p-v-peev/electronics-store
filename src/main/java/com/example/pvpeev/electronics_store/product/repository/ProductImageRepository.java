package com.example.pvpeev.electronics_store.product.repository;

import com.example.pvpeev.electronics_store.product.entity.ProductImageEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductImageRepository extends ListCrudRepository<ProductImageEntity, UUID> {

    @Modifying
    @Query("DELETE FROM product_image WHERE id = :id")
    int deleteByIdWithCount(UUID id);

    List<ProductImageEntity> findAllByProductId(UUID id);

}
