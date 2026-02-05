package com.example.pvpeev.electronics_store.product.service;

import com.example.pvpeev.electronics_store.product.dto.ProductBrandRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductBrandResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductBrandEntity;
import com.example.pvpeev.electronics_store.product.mapper.ProductBrandMapper;
import com.example.pvpeev.electronics_store.product.mapper.ProductBrandMapperImpl;
import com.example.pvpeev.electronics_store.product.repository.ProductBrandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductBrandServiceTest {

    @Mock
    private ProductBrandRepository productBrandRepository;

    @Spy
    private ProductBrandMapper productBrandMapper = new ProductBrandMapperImpl();

    @InjectMocks
    private ProductBrandService productBrandService;

    @Test
    public void testGetAll() {
        final ProductBrandEntity productEntity = new ProductBrandEntity(1, "Sony");
        final ProductBrandResponse expectedResponse = new ProductBrandResponse(productEntity.getId(), productEntity.getName());

        when(productBrandRepository.findAll()).thenReturn(List.of(productEntity));

        assertThat(productBrandService.getAll())
                .as("The response must contain exactly one element")
                .singleElement()
                .as("The response must be equal to the expected response")
                .isEqualTo(expectedResponse);

        verify(productBrandRepository, times(1)).findAll();
        verify(productBrandMapper, times(1)).toResponse(productEntity);
    }

    @Test
    public void testCreate() {
        final ProductBrandRequest request = new ProductBrandRequest("Sony");
        final ProductBrandEntity entityToSave = new ProductBrandEntity(null, request.getName());
        final ProductBrandEntity savedEntity = new ProductBrandEntity(1, request.getName());

        when(productBrandRepository.save(entityToSave)).thenReturn(savedEntity);

        final ProductBrandResponse expectedResponse = new ProductBrandResponse(1, request.getName());
        assertThat(productBrandService.create(request))
                .as("The response must be equal to the expected one")
                .isEqualTo(expectedResponse);

        verify(productBrandMapper, times(1)).toEntity(request);
        verify(productBrandRepository, times(1)).save(entityToSave);
        verify(productBrandMapper, times(1)).toResponse(savedEntity);
    }
}
