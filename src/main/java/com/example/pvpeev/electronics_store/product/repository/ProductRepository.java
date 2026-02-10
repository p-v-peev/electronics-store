package com.example.pvpeev.electronics_store.product.repository;

import com.example.pvpeev.electronics_store.product.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends ListCrudRepository<ProductEntity, UUID>, ListPagingAndSortingRepository<ProductEntity, UUID> {

    @Modifying
    @Query("UPDATE product SET deleted = true WHERE id = :id")
    int softDeleteById(UUID id);

    boolean existsByIdAndDeletedIsFalse(UUID id);

    Page<ProductEntity> findAllByProductCategoryId(Integer productCategoryId, Pageable pageable);

    boolean existsByProductCategoryId(Integer id);
}
