package com.example.pvpeev.electronics_store.product.repository;

import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import com.example.pvpeev.electronics_store.repository.BaseRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NON_TEST)
public class ProductCategoryRepositoryTest extends BaseRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_VERSION);

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
                .as("Second category with the same name can't be added twice")
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

}
