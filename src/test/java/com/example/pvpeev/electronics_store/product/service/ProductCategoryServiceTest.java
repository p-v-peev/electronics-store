package com.example.pvpeev.electronics_store.product.service;

import com.example.pvpeev.electronics_store.product.dto.ProductCategoryRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductCategoryResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import com.example.pvpeev.electronics_store.product.mapper.ProductCategoryMapper;
import com.example.pvpeev.electronics_store.product.mapper.ProductCategoryMapperImpl;
import com.example.pvpeev.electronics_store.product.repository.ProductCategoryRepository;
import com.example.pvpeev.electronics_store.product.repository.ProductRepository;
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
public class ProductCategoryServiceTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Spy
    private ProductCategoryMapper productCategoryMapper = new ProductCategoryMapperImpl();

    @InjectMocks
    private ProductCategoryService productCategoryService;

    @Test
    public void testFindAllWhenNoCategoriesExistReturnsEmptyList() {
        when(productCategoryRepository.findAll()).thenReturn(List.of());

        assertThat(productCategoryService.findAll())
                .as("The list must be empty")
                .isEmpty();

        verify(productCategoryMapper, times(0)).toResponse(any(ProductCategoryEntity.class));
    }

    @Test
    public void testFindAllMapsTheResponseProperly() {
        final ProductCategoryEntity categoryEntity = new ProductCategoryEntity(1, "smartphones", "Smartphones", "Smartphones");
        when(productCategoryRepository.findAll()).thenReturn(List.of(categoryEntity));

        final ProductCategoryResponse expectedResponse = new ProductCategoryResponse(categoryEntity.getId(), categoryEntity.getPath(), categoryEntity.getName(), categoryEntity.getDescription());
        assertThat(productCategoryService.findAll())
                .as("The list must have exactly one element")
                .singleElement()
                .as("The element must be equal to the expected element")
                .isEqualTo(expectedResponse);

        verify(productCategoryMapper, times(1)).toResponse(categoryEntity);
    }

    @Test
    public void testCreateReturnsTheExpectedResponse() {
        final ProductCategoryRequest categoryRequest = new ProductCategoryRequest("smartphones", "Smartphones", "Smartphones");
        final ProductCategoryEntity entityToSave = new ProductCategoryEntity(null, categoryRequest.getPath(), categoryRequest.getName(), categoryRequest.getDescription());
        final ProductCategoryEntity savedEntity = new ProductCategoryEntity(1, entityToSave.getPath(), entityToSave.getName(), entityToSave.getDescription());

        when(productCategoryRepository.save(entityToSave)).thenReturn(savedEntity);

        final ProductCategoryResponse expectedResponse = new ProductCategoryResponse(savedEntity.getId(), savedEntity.getPath(), savedEntity.getName(), savedEntity.getDescription());
        assertThat(productCategoryService.create(categoryRequest))
                .as("The response must be equal to the expected response")
                .isEqualTo(expectedResponse);

        verify(productCategoryMapper, times(1)).toEntity(categoryRequest);
        verify(productCategoryRepository, times(1)).save(entityToSave);
        verify(productCategoryMapper, times(1)).toResponse(savedEntity);
    }

    @Test
    public void testDeleteCategoryWithNoProducts() {
        final Integer id = 1;
        productCategoryService.deleteById(id);

        verify(productCategoryRepository, times(1)).deleteById(id);
    }

}
