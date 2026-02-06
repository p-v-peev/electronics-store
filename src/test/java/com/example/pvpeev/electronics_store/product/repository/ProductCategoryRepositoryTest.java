package com.example.pvpeev.electronics_store.product.repository;

import com.example.pvpeev.electronics_store.product.entity.ProductBrandEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductEntity;
import com.example.pvpeev.electronics_store.repository.BaseRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;

public class ProductCategoryRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductBrandRepository productBrandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testAddTheSameCategoryNameThrowsException() {
        final String name = "Smartphones";

        productCategoryRepository.save(new ProductCategoryEntity(null, "path1", name, "Smartphones description"));

        final RuntimeException exception = catchRuntimeException(() -> productCategoryRepository.save(new ProductCategoryEntity(null, "path2", name, "Smartphones description")));
        assertThat(exception)
                .as("Second category with the same name can't be added twice")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void testAddTheSameCategoryPathThrowsException() {
        final String path = "smartphones";

        productCategoryRepository.save(new ProductCategoryEntity(null, path, "Smartphones", "Smartphones description"));

        final RuntimeException exception = catchRuntimeException(() -> productCategoryRepository.save(new ProductCategoryEntity(null, path, "Laptops", "Smartphones description")));
        assertThat(exception)
                .as("Second category with the same path can't be added twice")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void testFindByUnexistingPathReturnsEmpty() {
        assertThat(productCategoryRepository.findByPath("unexistng-path"))
                .isEmpty();
    }

    @Test
    public void testFindByPath() {
        final String path = "smartphones";

        final ProductCategoryEntity save = productCategoryRepository.save(new ProductCategoryEntity(null, path, "Smartphones", "Smartphones description"));
        assertThat(productCategoryRepository.findByPath(path))
                .as("The repository must find the item saved above")
                .isPresent()
                .get()
                .as("The result must be equal to the saved entity")
                .isEqualTo(save);
    }

    @Test
    public void testDeleteCategoryWithProductsThrowsException() {
        final ProductCategoryEntity smartphones = productCategoryRepository.save(new ProductCategoryEntity(null, "smartphones", "Smartphones", "Smartphones description"));
        final ProductBrandEntity brand = productBrandRepository.save(new ProductBrandEntity(null, "Apple"));
        productRepository.save(new ProductEntity(null, smartphones.getId(), brand.getId(), "Apple iPhone 17", "Apple iPhone 17", 1000, 10, "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1"));

        final RuntimeException exception = catchRuntimeException(() -> productCategoryRepository.deleteById(smartphones.getId()));
        assertThat(exception)
                .as("Exception must be thrown by the database, because if the existing relation")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}
