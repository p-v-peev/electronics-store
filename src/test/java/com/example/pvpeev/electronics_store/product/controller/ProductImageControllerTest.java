package com.example.pvpeev.electronics_store.product.controller;

import com.example.pvpeev.electronics_store.blob.BlobStorageService;
import com.example.pvpeev.electronics_store.product.dto.ProductImageResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductBrandEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductEntity;
import com.example.pvpeev.electronics_store.product.repository.ProductBrandRepository;
import com.example.pvpeev.electronics_store.product.repository.ProductCategoryRepository;
import com.example.pvpeev.electronics_store.product.repository.ProductImageRepository;
import com.example.pvpeev.electronics_store.product.repository.ProductRepository;
import com.example.pvpeev.electronics_store.product.service.ProductImageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.example.pvpeev.electronics_store.TextConstants.PRODUCT_IMAGES_STORAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductImageControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private ProductBrandRepository productBrandRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private BlobStorageService blobStorageService;

    private final Set<String> blobsToDelete = new HashSet<>();

    @AfterEach
    public void afterEachTest() {
        blobsToDelete.forEach(blobId -> blobStorageService.delete(PRODUCT_IMAGES_STORAGE.getValue(), blobId));
        productImageRepository.deleteAll();
        productRepository.deleteAll();
        productCategoryRepository.deleteAll();
        productBrandRepository.deleteAll();
    }

    @Test
    public void testDeleteUnexistingImageReturnsNotFound() {
        assertThat(mockMvcTester.delete()
                .uri(ProductImageController.PATH + "/" + UUID.randomUUID()))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteExistingImage() throws IOException {
        final ProductBrandEntity productBrandEntity = productBrandRepository.save(new ProductBrandEntity(null, "Sony"));
        final ProductCategoryEntity productCategoryEntity = productCategoryRepository.save(new ProductCategoryEntity(null, "consoles", "Consoles", "Consoles"));
        final ProductEntity productEntity = productRepository.save(new ProductEntity(null, productCategoryEntity.getId(), productBrandEntity.getId(), "Playstation 5", "Playstation 5", 1000, 10, "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1", false));
        final Resource ps5Image = new ClassPathResource("ps5.avif");
        final MockMultipartFile image = new MockMultipartFile("image", ps5Image.getFilename(), "image/avif", ps5Image.getContentAsByteArray());

        final ProductImageResponse productImageResponse = productImageService.create(image, productEntity.getId());

        final String imageUrl = productImageResponse.getImageUrl();
        blobsToDelete.add(imageUrl.substring(imageUrl.lastIndexOf('/') + 1));

        assertThat(mockMvcTester.delete().uri(ProductImageController.PATH + "/" + productImageResponse.getId()))
                .as("The service must return HTTP 204")
                .hasStatus(HttpStatus.NO_CONTENT);

        assertThat(mockMvcTester.get()
                .uri(ProductController.PATH + "/" + productEntity.getId() + "/images"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(ProductImageResponse.class))
                .as("The product must not have any images at this point")
                .isEmpty();

        assertThat(productImageRepository.findById(productImageResponse.getId()))
                .as("The record in the database must be deleted")
                .isEmpty();

        final HttpStatusCode statusCode = RestClient.create()
                .get()
                .uri(imageUrl)
                .exchange((req, resp) -> resp.getStatusCode());
        assertThat(statusCode)
                .as("The image must be deleted in the blob storage")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
