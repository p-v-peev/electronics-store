package com.example.pvpeev.electronics_store.product.repository;

import com.example.pvpeev.electronics_store.product.entity.ProductBrandEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductImageEntity;
import com.example.pvpeev.electronics_store.repository.BaseRepositoryTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;

public class ProductImageRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductBrandRepository productBrandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testFindByUnexistingProductIdReturnsEmptyList() {
        assertThat(productImageRepository.findAllByProductId(UUID.randomUUID()))
                .as("The repository must return empty list")
                .isEmpty();
    }

    @Test
    public void testFindByProductId() {
        final ProductBrandEntity productBrandEntity = productBrandRepository.save(getProductBrandEntity());
        final ProductCategoryEntity productCategoryEntity = productCategoryRepository.save(getProductCategoryEntity());
        final ProductEntity productEntity = productRepository.save(getProductEntity(productCategoryEntity, productBrandEntity));
        final ProductImageEntity productImageEntity = productImageRepository.save(getProductImageEntity(productEntity, "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1"));

        assertThat(productImageRepository.findAllByProductId(productEntity.getId()))
                .as("The repository must return list with exactly one entity")
                .singleElement()
                .isEqualTo(productImageEntity);
    }

    @Test
    public void testDeleteByUnexistingIdReturnsZero() {
        assertThat(productImageRepository.deleteByIdWithCount(UUID.randomUUID()))
                .isEqualTo(0);
    }

    @Test
    public void testDeleteByIdReturnsOne() {
        final ProductBrandEntity productBrandEntity = productBrandRepository.save(getProductBrandEntity());
        final ProductCategoryEntity productCategoryEntity = productCategoryRepository.save(getProductCategoryEntity());
        final ProductEntity productEntity = productRepository.save(getProductEntity(productCategoryEntity, productBrandEntity));
        final ProductImageEntity productImageEntity1 = productImageRepository.save(getProductImageEntity(productEntity, "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1"));
        final ProductImageEntity productImageEntity2 = productImageRepository.save(getProductImageEntity(productEntity, "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a2"));

        assertThat(productImageRepository.deleteByIdWithCount(productImageEntity1.getId()))
                .as("Exactly one item must be deleted")
                .isEqualTo(1);

        assertThat(productImageRepository.deleteByIdWithCount(productImageEntity2.getId()))
                .as("Exactly one item must be deleted")
                .isEqualTo(1);

        assertThat(productImageRepository.findAll())
                .as("The list must be empty")
                .isEmpty();
    }

    @Test
    public void testAddTheSameImageForSingleProductThrowsException() {
        final ProductBrandEntity productBrandEntity = productBrandRepository.save(getProductBrandEntity());
        final ProductCategoryEntity productCategoryEntity = productCategoryRepository.save(getProductCategoryEntity());
        final ProductEntity productEntity = productRepository.save(getProductEntity(productCategoryEntity, productBrandEntity));

        productImageRepository.save(getProductImageEntity(productEntity, "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1"));
        final RuntimeException exception = catchRuntimeException(() -> productImageRepository.save(getProductImageEntity(productEntity, "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1")));

        assertThat(exception)
                .as("One product can't have the same image twice")
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    public void testAddImageForUnexistingProductThrowsException() {
        final RuntimeException exception = catchRuntimeException(() -> productImageRepository.save(getProductImageEntity(UUID.randomUUID(), "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1")));

        assertThat(exception)
                .as("Images can't be added for unexisting products")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private static @NotNull ProductImageEntity getProductImageEntity(ProductEntity productEntity, String url) {
        return getProductImageEntity(productEntity.getId(), url);
    }

    private static @NotNull ProductImageEntity getProductImageEntity(UUID productId, String url) {
        return new ProductImageEntity(null, productId, url);
    }

    private static @NotNull ProductEntity getProductEntity(ProductCategoryEntity productCategoryEntity, ProductBrandEntity productBrandEntity) {
        return new ProductEntity(null, productCategoryEntity.getId(), productBrandEntity.getId(), "iPhone 17", "iPhone 17", 1200, 10, "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1", false);
    }

    private static @NotNull ProductCategoryEntity getProductCategoryEntity() {
        return new ProductCategoryEntity(null, "smartphones", "Smartphones", "Smartphones category");
    }

    private static @NotNull ProductBrandEntity getProductBrandEntity() {
        return new ProductBrandEntity(null, "Apple");
    }
}
