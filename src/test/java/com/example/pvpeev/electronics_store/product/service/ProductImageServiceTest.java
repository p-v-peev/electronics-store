package com.example.pvpeev.electronics_store.product.service;

import com.example.pvpeev.electronics_store.advice.exception.FileDeleteException;
import com.example.pvpeev.electronics_store.advice.exception.FileUploadException;
import com.example.pvpeev.electronics_store.advice.exception.ResourceNotFoundException;
import com.example.pvpeev.electronics_store.blob.BlobStorageService;
import com.example.pvpeev.electronics_store.product.dto.ProductImageResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductImageEntity;
import com.example.pvpeev.electronics_store.product.mapper.ProductImageMapper;
import com.example.pvpeev.electronics_store.product.mapper.ProductImageMapperImpl;
import com.example.pvpeev.electronics_store.product.repository.ProductImageRepository;
import com.example.pvpeev.electronics_store.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.pvpeev.electronics_store.TextConstants.PRODUCT_IMAGES_STORAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductImageServiceTest {

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BlobStorageService blobStorageService;

    @Spy
    private ProductImageMapper productImageMapper = new ProductImageMapperImpl();

    @InjectMocks
    private ProductImageService productImageService;

    @Test
    public void testGetByProductIdThrowsExceptionIfTheProductDoesntExist() {
        final UUID productId = UUID.randomUUID();
        when(productRepository.existsByIdAndDeletedIsFalse(productId)).thenReturn(false);

        final RuntimeException exception = catchRuntimeException(() -> productImageService.getByProductId(productId));
        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404")
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, times(1)).existsByIdAndDeletedIsFalse(productId);
        verify(productImageRepository, times(0)).findAllByProductId(productId);
        verify(productImageMapper, times(0)).toResponse(any(ProductImageEntity.class));
    }

    @Test
    public void testGetByProductIdWithExistingProduct() {
        final ProductImageEntity productImageEntity = new ProductImageEntity(UUID.randomUUID(), UUID.randomUUID(), "http://localhost:9000/product-thumbnails/" + UUID.randomUUID());

        when(productRepository.existsByIdAndDeletedIsFalse(productImageEntity.getProductId())).thenReturn(true);
        when(productImageRepository.findAllByProductId(productImageEntity.getProductId())).thenReturn(List.of(productImageEntity));

        final ProductImageResponse expectedResponse = new ProductImageResponse(productImageEntity.getId(), productImageEntity.getProductId(), productImageEntity.getImageUrl());
        assertThat(productImageService.getByProductId(productImageEntity.getProductId()))
                .isEqualTo(List.of(expectedResponse));

        verify(productRepository, times(1)).existsByIdAndDeletedIsFalse(productImageEntity.getProductId());
        verify(productImageRepository, times(1)).findAllByProductId(productImageEntity.getProductId());
        verify(productImageMapper, times(1)).toResponse(productImageEntity);
    }

    @Test
    public void testCreateProductImage() {
        final MockMultipartFile image = new MockMultipartFile("image.png", (byte[]) null);
        final ProductImageEntity entityToSave = new ProductImageEntity(null, UUID.randomUUID(), "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1");
        final ProductImageEntity savedEntity = new ProductImageEntity(UUID.randomUUID(), entityToSave.getProductId(), entityToSave.getImageUrl());

        when(productRepository.existsByIdAndDeletedIsFalse(entityToSave.getProductId())).thenReturn(true);
        when(blobStorageService.getFileUrl(eq(PRODUCT_IMAGES_STORAGE.getValue()), any(UUID.class))).thenReturn(entityToSave.getImageUrl());
        when(productImageMapper.toEntity(entityToSave.getProductId(), entityToSave.getImageUrl())).thenReturn(entityToSave);
        when(productImageRepository.save(entityToSave)).thenReturn(savedEntity);

        final ProductImageResponse expectedResponse = new ProductImageResponse(savedEntity.getId(), savedEntity.getProductId(), savedEntity.getImageUrl());
        assertThat(productImageService.create(image, entityToSave.getProductId()))
                .as("The response must be equal to the expected response")
                .isEqualTo(expectedResponse);

        final InOrder inOrder = inOrder(blobStorageService, productImageMapper, productImageRepository, blobStorageService, productImageRepository, productImageMapper);
        inOrder.verify(blobStorageService, times(1)).getFileUrl(eq(PRODUCT_IMAGES_STORAGE.getValue()), any(UUID.class));
        inOrder.verify(productImageMapper, times(1)).toEntity(entityToSave.getProductId(), entityToSave.getImageUrl());
        inOrder.verify(productImageRepository, times(1)).save(entityToSave);
        inOrder.verify(blobStorageService).uploadFile(eq(PRODUCT_IMAGES_STORAGE.getValue()), any(UUID.class), eq(image));
        inOrder.verify(productImageRepository, times(0)).deleteById(savedEntity.getId());
        inOrder.verify(productImageMapper, times(1)).toResponse(savedEntity);
    }

    @Test
    public void testCreateProductImageDbRecordIsDeletedOnBlobUploadFailure() {
        final MockMultipartFile image = new MockMultipartFile("image.png", (byte[]) null);
        final ProductImageEntity entityToSave = new ProductImageEntity(null, UUID.randomUUID(), "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1");
        final ProductImageEntity savedEntity = new ProductImageEntity(UUID.randomUUID(), entityToSave.getProductId(), entityToSave.getImageUrl());

        when(productRepository.existsByIdAndDeletedIsFalse(entityToSave.getProductId())).thenReturn(true);
        when(blobStorageService.getFileUrl(eq(PRODUCT_IMAGES_STORAGE.getValue()), any(UUID.class))).thenReturn(entityToSave.getImageUrl());
        when(productImageMapper.toEntity(entityToSave.getProductId(), entityToSave.getImageUrl())).thenReturn(entityToSave);
        when(productImageRepository.save(entityToSave)).thenReturn(savedEntity);
        doThrow(new FileUploadException()).when(blobStorageService).uploadFile(eq(PRODUCT_IMAGES_STORAGE.getValue()), any(UUID.class), eq(image));

        final RuntimeException exception = catchRuntimeException(() -> productImageService.create(image, entityToSave.getProductId()));
        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 500")
                .isInstanceOf(FileUploadException.class);

        final InOrder inOrder = inOrder(blobStorageService, productImageMapper, productImageRepository, blobStorageService, productImageRepository, productImageMapper);
        inOrder.verify(blobStorageService, times(1)).getFileUrl(eq(PRODUCT_IMAGES_STORAGE.getValue()), any(UUID.class));
        inOrder.verify(productImageMapper, times(1)).toEntity(entityToSave.getProductId(), entityToSave.getImageUrl());
        inOrder.verify(productImageRepository, times(1)).save(entityToSave);
        inOrder.verify(blobStorageService).uploadFile(eq(PRODUCT_IMAGES_STORAGE.getValue()), any(UUID.class), eq(image));
        inOrder.verify(productImageRepository, times(1)).deleteById(savedEntity.getId());
        inOrder.verify(productImageMapper, times(0)).toResponse(savedEntity);
    }

    @Test
    public void testDeleteUnexistingImageThrowsException() {
        final String blobStorageKey = UUID.randomUUID().toString();
        final ProductImageEntity imageEntity = new ProductImageEntity(UUID.randomUUID(), UUID.randomUUID(), "http://localhost:9000/product-thumbnails/" + blobStorageKey);

        when(productImageRepository.findById(imageEntity.getId())).thenReturn(Optional.of(imageEntity));
        when(productImageRepository.deleteByIdWithCount(imageEntity.getId())).thenReturn(0);

        final RuntimeException exception = catchRuntimeException(() -> productImageService.delete(imageEntity.getId()));
        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 404")
                .isInstanceOf(ResourceNotFoundException.class);

        final InOrder inOrder = inOrder(productImageRepository, blobStorageService, productImageRepository);
        inOrder.verify(productImageRepository, times(1)).findById(imageEntity.getId());
        inOrder.verify(blobStorageService, times(1)).delete(PRODUCT_IMAGES_STORAGE.getValue(), blobStorageKey);
        inOrder.verify(productImageRepository, times(1)).deleteByIdWithCount(imageEntity.getId());
    }

    @Test
    public void testDeleteImageCallsTheBlobServiceBeforeItDeletesItFromTheDB() {
        final String blobStorageKey = UUID.randomUUID().toString();
        final ProductImageEntity imageEntity = new ProductImageEntity(UUID.randomUUID(), UUID.randomUUID(), "http://localhost:9000/product-thumbnails/" + blobStorageKey);

        when(productImageRepository.findById(imageEntity.getId())).thenReturn(Optional.of(imageEntity));
        when(productImageRepository.deleteByIdWithCount(imageEntity.getId())).thenReturn(1);

        productImageService.delete(imageEntity.getId());

        final InOrder inOrder = inOrder(productImageRepository, blobStorageService, productImageRepository);
        inOrder.verify(productImageRepository, times(1)).findById(imageEntity.getId());
        inOrder.verify(blobStorageService, times(1)).delete(PRODUCT_IMAGES_STORAGE.getValue(), blobStorageKey);
        inOrder.verify(productImageRepository, times(1)).deleteByIdWithCount(imageEntity.getId());
    }

    @Test
    public void testOnFileDeleteExceptionTheRecordIsNotDeletedFromTheDb() {
        final String blobStorageKey = UUID.randomUUID().toString();
        final ProductImageEntity imageEntity = new ProductImageEntity(UUID.randomUUID(), UUID.randomUUID(), "http://localhost:9000/product-thumbnails/" + blobStorageKey);

        when(productImageRepository.findById(imageEntity.getId())).thenReturn(Optional.of(imageEntity));
        doThrow(new FileDeleteException()).when(blobStorageService).delete(PRODUCT_IMAGES_STORAGE.getValue(), blobStorageKey);

        final RuntimeException exception = catchRuntimeException(() -> productImageService.delete(imageEntity.getId()));
        assertThat(exception)
                .as("Exception must be thrown to feed properly the controller advice, so the client gets HTTP 500")
                .isInstanceOf(FileDeleteException.class);


        final InOrder inOrder = inOrder(productImageRepository, blobStorageService, productImageRepository);
        inOrder.verify(productImageRepository, times(1)).findById(imageEntity.getId());
        inOrder.verify(blobStorageService, times(1)).delete(PRODUCT_IMAGES_STORAGE.getValue(), blobStorageKey);
        inOrder.verify(productImageRepository, times(0)).deleteByIdWithCount(imageEntity.getId());
    }

}
