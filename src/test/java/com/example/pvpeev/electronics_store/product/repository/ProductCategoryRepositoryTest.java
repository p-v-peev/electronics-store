package com.example.pvpeev.electronics_store.product.repository;

import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import com.example.pvpeev.electronics_store.repository.BaseRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThatList;

public class ProductCategoryRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

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
    public void testDeleteByUnexistingIdReturnsZero() {
        assertThat(productCategoryRepository.deleteByIdWithCount(1))
                .isEqualTo(0);
    }

    @Test
    public void testDeleteByIdReturnsOne() {
        final ProductCategoryEntity smartphones = productCategoryRepository.save(new ProductCategoryEntity(null, "smartphones", "Smartphones", "Smartphones description"));
        final ProductCategoryEntity laptops = productCategoryRepository.save(new ProductCategoryEntity(null, "laptops", "Laptops", "Laptops description"));

        assertThat(productCategoryRepository.deleteByIdWithCount(smartphones.getId()))
                .as("Exactly one item must be deleted")
                .isEqualTo(1);

        assertThat(productCategoryRepository.deleteByIdWithCount(laptops.getId()))
                .as("Exactly one item must be deleted")
                .isEqualTo(1);

        assertThatList(productCategoryRepository.findAll())
                .as("The list must be empty")
                .isEmpty();
    }

}
