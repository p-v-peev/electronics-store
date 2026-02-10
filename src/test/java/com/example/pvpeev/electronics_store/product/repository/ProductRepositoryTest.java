package com.example.pvpeev.electronics_store.product.repository;

import com.example.pvpeev.electronics_store.product.entity.ProductBrandEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductEntity;
import com.example.pvpeev.electronics_store.repository.BaseRepositoryTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;

public class ProductRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductBrandRepository productBrandRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Test
    public void testExistsByProductCategoryReturnsTrue() {
        final ProductBrandEntity productBrandEntity = productBrandRepository.save(getProductBrandEntity());
        final ProductCategoryEntity productCategoryEntity = productCategoryRepository.save(getProductCategoryEntity());
        productRepository.save(getProductEntity(productCategoryEntity, productBrandEntity, "iPhone 16", "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1"));

        assertThat(productRepository.existsByProductCategoryId(productCategoryEntity.getId()))
                .as("The result must be true, because of the product inserted above")
                .isTrue();
    }

    @Test
    public void testExistsByProductCategoryReturnsFalseIfThereAreNoProducts() {
        final ProductCategoryEntity productCategoryEntity = productCategoryRepository.save(getProductCategoryEntity());

        assertThat(productRepository.existsByProductCategoryId(productCategoryEntity.getId()))
                .as("There are no products in this category")
                .isFalse();
    }

    @Test()
    public void testExistsByProductCategoryReturnsFalseOnUnexistingCategory() {
        assertThat(productRepository.existsByProductCategoryId(1))
                .as("There are no products in this category")
                .isFalse();
    }

    @Test
    public void findAllByProductCategoryIdReturnEmptyPageableOnMissingCategory() {
        assertThat(productRepository.findAllByProductCategoryId(1, PageRequest.ofSize(10)))
                .as("The page must be empty, because there are no products in this category")
                .hasSize(0);

    }

    @Test
    public void findAllByProductCategoryIdPaginationSortWorks() {
        final ProductBrandEntity productBrandEntity = productBrandRepository.save(getProductBrandEntity());
        final ProductCategoryEntity productCategoryEntity = productCategoryRepository.save(getProductCategoryEntity());
        final ProductEntity productEntity1 = productRepository.save(getProductEntity(productCategoryEntity, productBrandEntity, "iPhone 16", "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1"));
        final ProductEntity productEntity2 = productRepository.save(getProductEntity(productCategoryEntity, productBrandEntity, "iPhone 17", "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a2"));

        // iPhone 16 is less than iPhone 17
        final Sort.TypedSort<String> sort = Sort.sort(ProductEntity.class).by(ProductEntity::getName);

        assertThat(productRepository.findAllByProductCategoryId(productCategoryEntity.getId(), PageRequest.of(0, 1, sort.ascending())))
                .as("The page size must be one")
                .singleElement()
                .as("The page must contain only the first product added above")
                .isEqualTo(productEntity1);


        assertThat(productRepository.findAllByProductCategoryId(productCategoryEntity.getId(), PageRequest.of(0, 1, sort.descending())))
                .as("The page size must be one")
                .singleElement()
                .as("The page must contain only the second product added above")
                .isEqualTo(productEntity2);
    }

    @Test
    public void findAllByProductCategoryIdPaginationWorks() {
        final ProductBrandEntity productBrandEntity = productBrandRepository.save(getProductBrandEntity());
        final ProductCategoryEntity productCategoryEntity = productCategoryRepository.save(getProductCategoryEntity());
        final ProductEntity productEntity1 = productRepository.save(getProductEntity(productCategoryEntity, productBrandEntity, "iPhone 16", "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1"));
        final ProductEntity productEntity2 = productRepository.save(getProductEntity(productCategoryEntity, productBrandEntity, "iPhone 17", "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a2"));

        final Sort sort = Sort.sort(ProductEntity.class).by(ProductEntity::getName).ascending();

        assertThat(productRepository.findAllByProductCategoryId(productCategoryEntity.getId(), PageRequest.of(0, 1, sort)))
                .as("The page size must be one")
                .singleElement()
                .as("The page must contain only the first product added above")
                .isEqualTo(productEntity1);


        assertThat(productRepository.findAllByProductCategoryId(productCategoryEntity.getId(), PageRequest.of(1, 1, sort)))
                .as("The page must be one")
                .singleElement()
                .as("The page must contain only the second product added above")
                .isEqualTo(productEntity2);

        assertThat(productRepository.findAllByProductCategoryId(productCategoryEntity.getId(), PageRequest.of(0, 2)))
                .as("The method must return the two products saved above")
                .hasSize(2)
                .as("The page must contain the both products")
                .containsExactlyInAnyOrder(productEntity1, productEntity2);
    }

    @Test
    public void testDeleteByUnexistingIdReturnsZero() {
        assertThat(productRepository.softDeleteById(UUID.randomUUID()))
                .as("Nothing must be deleted")
                .isEqualTo(0);
    }

    @Test
    public void testDeleteByIdReturnsOne() {
        final ProductBrandEntity productBrandEntity = productBrandRepository.save(getProductBrandEntity());
        final ProductCategoryEntity productCategoryEntity = productCategoryRepository.save(getProductCategoryEntity());
        final ProductEntity productEntity1 = productRepository.save(getProductEntity(productCategoryEntity, productBrandEntity, "iPhone 16", "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1"));
        final ProductEntity productEntity2 = productRepository.save(getProductEntity(productCategoryEntity, productBrandEntity, "iPhone 17", "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a2"));

        assertThat(productRepository.softDeleteById(productEntity1.getId()))
                .as("Exactly one item must be deleted")
                .isEqualTo(1);

        assertThat(productRepository.softDeleteById(productEntity2.getId()))
                .as("Exactly one item must be deleted")
                .isEqualTo(1);

        assertThat(productRepository.existsByIdAndDeletedIsFalse(productEntity1.getId()))
                .as("The item must be soft deleted")
                .isFalse();

        assertThat(productRepository.existsByIdAndDeletedIsFalse(productEntity2.getId()))
                .as("The item must be soft deleted")
                .isFalse();

        assertThat(productRepository.findById(productEntity1.getId()))
                .as("The item must be soft deleted, but still in the database")
                .isNotEmpty()
                .get()
                .extracting(ProductEntity::isDeleted)
                .isEqualTo(true);

        assertThat(productRepository.findById(productEntity2.getId()))
                .as("The item must be soft deleted, but still in the database")
                .isNotEmpty()
                .get()
                .extracting(ProductEntity::isDeleted)
                .isEqualTo(true);

        assertThat(productRepository.findAll())
                .as("The items must be sof deleted, but still in the database")
                .hasSize(2);
    }

    @Test
    public void testAddProductInUnexistingCategoryThrowsException() {
        final ProductBrandEntity productBrandEntity = productBrandRepository.save(getProductBrandEntity());

        final RuntimeException exception = catchRuntimeException(() -> productRepository.save(getProductEntity(1, productBrandEntity.getId(), "iPhone 16", "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1")));

        assertThat(exception)
                .as("The product must be from existing category")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void testAddProductWithUnexistingBrandThrowsException() {
        final ProductCategoryEntity productCategoryEntity = productCategoryRepository.save(getProductCategoryEntity());

        final RuntimeException exception = catchRuntimeException(() -> productRepository.save(getProductEntity(productCategoryEntity.getId(), 1, "iPhone 16", "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1")));

        assertThat(exception)
                .as("The product must be from existing brand")
                .isInstanceOf(DataIntegrityViolationException.class);
    }


    private static @NotNull ProductEntity getProductEntity(ProductCategoryEntity productCategoryEntity, ProductBrandEntity productBrandEntity, String product, String thumbnailUrl) {
        return getProductEntity(productCategoryEntity.getId(), productBrandEntity.getId(), product, thumbnailUrl);
    }

    private static @NotNull ProductEntity getProductEntity(Integer productCategoryId, Integer productBrandId, String product, String thumbnailUrl) {
        return new ProductEntity(null, productCategoryId, productBrandId, product, product, 1200, 10, thumbnailUrl, false);
    }

    private static @NotNull ProductCategoryEntity getProductCategoryEntity() {
        return new ProductCategoryEntity(null, "smartphones", "Smartphones", "Smartphones category");
    }

    private static @NotNull ProductBrandEntity getProductBrandEntity() {
        return new ProductBrandEntity(null, "Apple");

    }
}
