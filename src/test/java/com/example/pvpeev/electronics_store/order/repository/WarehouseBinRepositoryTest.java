package com.example.pvpeev.electronics_store.order.repository;

import com.example.pvpeev.electronics_store.order.entity.WarehouseBinEntity;
import com.example.pvpeev.electronics_store.repository.BaseRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;

public class WarehouseBinRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private WarehouseBinRepository warehouseBinRepository;

    @Test
    public void testSavingTheSameBinTwiceThrowsException() {
        final String binLabel = "A-12-3";
        warehouseBinRepository.save(new WarehouseBinEntity(null, binLabel));

        final RuntimeException exception = catchRuntimeException(() -> warehouseBinRepository.save(new WarehouseBinEntity(null, binLabel)));
        assertThat(exception)
                .as("Saving the same bin twice in not allowed")
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
