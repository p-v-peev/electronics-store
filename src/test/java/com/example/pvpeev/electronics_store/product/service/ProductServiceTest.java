package com.example.pvpeev.electronics_store.product.service;

import com.example.pvpeev.electronics_store.advice.exception.BadRequestException;
import com.example.pvpeev.electronics_store.advice.exception.FileUploadException;
import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.blob.BlobStorageService;
import com.example.pvpeev.electronics_store.product.dto.ProductRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductEntity;
import com.example.pvpeev.electronics_store.product.mapper.ProductMapper;
import com.example.pvpeev.electronics_store.product.mapper.ProductMapperImpl;
import com.example.pvpeev.electronics_store.product.repository.ProductCategoryRepository;
import com.example.pvpeev.electronics_store.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.pvpeev.electronics_store.TextConstants.PRODUCT_THUMBNAILS_STORAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private BlobStorageService blobStorageService;

    @Spy
    private ProductMapper productMapper = new ProductMapperImpl();

    @InjectMocks
    private ProductService productService;

    @Test
    public void testGetUnexistingPathThrowsException() {
        final String path = "unexisting";
        final PageRequest pageRequest = PageRequest.ofSize(10);

        when(productCategoryRepository.findByPath(path)).thenReturn(Optional.empty());

        final RuntimeException exception = catchRuntimeException(() -> productService.getAll(path, pageRequest));
        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404")
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productCategoryRepository, times(1)).findByPath(path);
        verify(productRepository, times(0)).findAllByProductCategoryId(any(Integer.class), eq(pageRequest));
        verify(productMapper, times(0)).toResponse(any(ProductEntity.class));
    }

    @Test
    public void testGetProductsFromExistingPath() {
        final PageRequest pageRequest = PageRequest.ofSize(10);
        final ProductCategoryEntity categoryEntity = new ProductCategoryEntity(1, "smartphones", "Smartphones", "Smartphones");
        final ProductEntity savedEntity = new ProductEntity(UUID.randomUUID(), categoryEntity.getId(), 1, "Apple iPhone 17", "Apple iPhone 17", 1000, 10, "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1");

        when(productCategoryRepository.findByPath(categoryEntity.getPath())).thenReturn(Optional.of(categoryEntity));
        when(productRepository.findAllByProductCategoryId(categoryEntity.getId(), pageRequest)).thenReturn(new PageImpl<>(List.of(savedEntity)));

        final ProductResponse expectedResponse = new ProductResponse(savedEntity.getId(), savedEntity.getProductCategoryId(), savedEntity.getProductBrandId(), savedEntity.getName(), savedEntity.getDescription(), savedEntity.getPrice(), savedEntity.getQuantityAvailable(), savedEntity.getThumbnailImageUrl());
        assertThat(productService.getAll(categoryEntity.getPath(), pageRequest))
                .as("The page must contain only one element")
                .singleElement()
                .as("The element must be equal to the expect response")
                .isEqualTo(expectedResponse);

        verify(productCategoryRepository, times(1)).findByPath(categoryEntity.getPath());
        verify(productRepository, times(1)).findAllByProductCategoryId(categoryEntity.getId(), pageRequest);
        verify(productMapper, times(1)).toResponse(savedEntity);
    }

    @Test
    public void testCreateByUnexistingPathThrowsException() {
        final String path = "unexisting";
        final ProductRequest productRequest = new ProductRequest(1, "Apple iPhone 17", "Apple iPhone 17", 1000, 10);
        final MockMultipartFile image = new MockMultipartFile("test", (byte[]) null);

        when(productCategoryRepository.findByPath(path)).thenReturn(Optional.empty());

        final RuntimeException exception = catchRuntimeException(() -> productService.create(productRequest, image, path));

        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404")
                .isInstanceOf(BadRequestException.class);

        verify(productCategoryRepository, times(1)).findByPath(path);
        verify(blobStorageService, times(0)).getFileUrl(eq(PRODUCT_THUMBNAILS_STORAGE.getValue()), any(UUID.class));
        verify(productMapper, times(0)).toEntity(eq(productRequest), any(Integer.class), any(String.class));
        verify(productRepository, times(0)).save(any(ProductEntity.class));
        verify(blobStorageService, times(0)).uploadFile(eq(PRODUCT_THUMBNAILS_STORAGE.getValue()), any(UUID.class), eq(image));
        verify(productRepository, times(0)).deleteById(any(UUID.class));
    }

    @Test
    public void testCreateByPathFileUploadFails() {
        final ProductCategoryEntity categoryEntity = new ProductCategoryEntity(1, "smartphones", "Smartphones", "Smartphones");
        final ProductRequest productRequest = new ProductRequest(1, "Apple iPhone 17", "Apple iPhone 17", 1000, 10);
        final ProductEntity productToSave = new ProductEntity(null, categoryEntity.getId(), productRequest.getProductBrandId(), productRequest.getName(), productRequest.getDescription(), productRequest.getPrice(), productRequest.getQuantityAvailable(), "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1");
        final ProductEntity savedProduct = new ProductEntity(UUID.randomUUID(), productToSave.getProductCategoryId(), productToSave.getProductBrandId(), productToSave.getName(), productToSave.getDescription(), productToSave.getPrice(), productToSave.getQuantityAvailable(), productToSave.getThumbnailImageUrl());
        final MockMultipartFile image = new MockMultipartFile("test", (byte[]) null);

        when(productCategoryRepository.findByPath(categoryEntity.getPath())).thenReturn(Optional.of(categoryEntity));
        when(blobStorageService.getFileUrl(eq(PRODUCT_THUMBNAILS_STORAGE.getValue()), any(UUID.class))).thenReturn(productToSave.getThumbnailImageUrl());
        when(productRepository.save(productToSave)).thenReturn(savedProduct);
        doThrow(new FileUploadException()).when(blobStorageService).uploadFile(eq(PRODUCT_THUMBNAILS_STORAGE.getValue()), any(UUID.class), eq(image));

        final RuntimeException exception = catchRuntimeException(() -> productService.create(productRequest, image, categoryEntity.getPath()));
        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 500")
                .isInstanceOf(FileUploadException.class);

        verify(productCategoryRepository, times(1)).findByPath(categoryEntity.getPath());
        verify(blobStorageService, times(1)).getFileUrl(eq(PRODUCT_THUMBNAILS_STORAGE.getValue()), any(UUID.class));
        verify(productMapper, times(1)).toEntity(productRequest, categoryEntity.getId(), productToSave.getThumbnailImageUrl());
        verify(productRepository, times(1)).save(productToSave);
        verify(blobStorageService, times(1)).uploadFile(eq(PRODUCT_THUMBNAILS_STORAGE.getValue()), any(UUID.class), eq(image));
        verify(productRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    public void testCreateByPathFileUpload() {
        final ProductCategoryEntity categoryEntity = new ProductCategoryEntity(1, "smartphones", "Smartphones", "Smartphones");
        final ProductRequest productRequest = new ProductRequest(1, "Apple iPhone 17", "Apple iPhone 17", 1000, 10);
        final ProductEntity productToSave = new ProductEntity(null, categoryEntity.getId(), productRequest.getProductBrandId(), productRequest.getName(), productRequest.getDescription(), productRequest.getPrice(), productRequest.getQuantityAvailable(), "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1");
        final ProductEntity savedProduct = new ProductEntity(UUID.randomUUID(), productToSave.getProductCategoryId(), productToSave.getProductBrandId(), productToSave.getName(), productToSave.getDescription(), productToSave.getPrice(), productToSave.getQuantityAvailable(), productToSave.getThumbnailImageUrl());
        final MockMultipartFile image = new MockMultipartFile("test", (byte[]) null);

        when(productCategoryRepository.findByPath(categoryEntity.getPath())).thenReturn(Optional.of(categoryEntity));
        when(blobStorageService.getFileUrl(eq(PRODUCT_THUMBNAILS_STORAGE.getValue()), any(UUID.class))).thenReturn(productToSave.getThumbnailImageUrl());
        when(productRepository.save(productToSave)).thenReturn(savedProduct);

        productService.create(productRequest, image, categoryEntity.getPath());

        verify(productCategoryRepository, times(1)).findByPath(categoryEntity.getPath());
        verify(blobStorageService, times(1)).getFileUrl(eq(PRODUCT_THUMBNAILS_STORAGE.getValue()), any(UUID.class));
        verify(productMapper, times(1)).toEntity(productRequest, categoryEntity.getId(), productToSave.getThumbnailImageUrl());
        verify(productRepository, times(1)).save(productToSave);
        verify(blobStorageService, times(1)).uploadFile(eq(PRODUCT_THUMBNAILS_STORAGE.getValue()), any(UUID.class), eq(image));
        verify(productRepository, times(0)).deleteById(any(UUID.class));
    }

    @Test
    public void testDeleteByUnexistingIdThrowsException() {
        final UUID id = UUID.randomUUID();
        when(productRepository.deleteByIdWithCount(id)).thenReturn(0);

        final RuntimeException exception = catchRuntimeException(() -> productService.deleteById(id));
        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404")
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void testDeleteById() {
        final UUID id = UUID.randomUUID();
        when(productRepository.deleteByIdWithCount(id)).thenReturn(1);

        productService.deleteById(id);
    }
}
