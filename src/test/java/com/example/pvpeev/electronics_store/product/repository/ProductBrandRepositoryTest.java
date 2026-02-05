package com.example.pvpeev.electronics_store.product.repository;

import com.example.pvpeev.electronics_store.product.entity.ProductBrandEntity;
import com.example.pvpeev.electronics_store.repository.BaseRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;

public class ProductBrandRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ProductBrandRepository productBrandRepository;

    @Test
    public void testAddTheSameBrandTwiceThrowsException() {

        productBrandRepository.save(new ProductBrandEntity(null, "Sony"));
        final RuntimeException exception = catchRuntimeException(() -> productBrandRepository.save(new ProductBrandEntity(null, "Sony")));

        assertThat(exception)
                .as("Only unique product brands are allowed")
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
