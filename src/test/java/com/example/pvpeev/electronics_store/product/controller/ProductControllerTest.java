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
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.example.pvpeev.electronics_store.TextConstants.PRODUCT_IMAGES_STORAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductBrandRepository productBrandRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

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
    public void testCreateRetrieveProductImage() throws IOException {

        final ProductBrandEntity productBrandEntity = productBrandRepository.save(new ProductBrandEntity(null, "Sony"));
        final ProductCategoryEntity productCategoryEntity = productCategoryRepository.save(new ProductCategoryEntity(null, "consoles", "Consoles", "Consoles"));
        final ProductEntity productEntity = productRepository.save(new ProductEntity(null, productCategoryEntity.getId(), productBrandEntity.getId(), "Playstation 5", "Playstation 5", 1000, 10, "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1", false));

        final Resource ps5Image = new ClassPathResource("ps5.avif");
        final MockMultipartFile image = new MockMultipartFile("image", ps5Image.getFilename(), "image/avif", ps5Image.getContentAsByteArray());

        final MvcTestResult productImageResult = mockMvcTester.post()
                .uri(ProductController.PATH + "/" + productEntity.getId() + "/images")
                .multipart()
                .file(image)
                .exchange();

        assertThat(productImageResult)
                .hasStatus(HttpStatus.CREATED)
                .containsHeader(HttpHeaders.LOCATION);

        final String location = productImageResult.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(location).isNotNull();

        final ProductImageResponse productImageResponse = objectMapper.readValue(productImageResult.getResponse().getContentAsByteArray(), ProductImageResponse.class);
        assertThat(productImageResponse)
                .satisfies(imr -> {
                    assertThat(imr.getId()).isNotNull();
                    assertThat(imr.getProductId()).isEqualTo(productEntity.getId());
                    assertThat(imr.getImageUrl()).isNotNull();
                });

        final String imageUrl = productImageResponse.getImageUrl();
        blobsToDelete.add(imageUrl.substring(imageUrl.lastIndexOf('/') + 1));

        assertThat(mockMvcTester.get()
                .uri(location))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(ProductImageResponse.class))
                .as("The product must have exactly one image")
                .singleElement()
                .as("The image must be equal to the created image")
                .isEqualTo(productImageResponse);
    }

    @Test
    public void testCreatedImageIsEqualToTheOriginal() throws IOException {

        final ProductBrandEntity productBrandEntity = productBrandRepository.save(new ProductBrandEntity(null, "Sony"));
        final ProductCategoryEntity productCategoryEntity = productCategoryRepository.save(new ProductCategoryEntity(null, "consoles", "Consoles", "Consoles"));
        final ProductEntity productEntity = productRepository.save(new ProductEntity(null, productCategoryEntity.getId(), productBrandEntity.getId(), "Playstation 5", "Playstation 5", 1000, 10, "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1", false));

        final Resource ps5Image = new ClassPathResource("ps5.avif");
        final MockMultipartFile image = new MockMultipartFile("image", ps5Image.getFilename(), "image/avif", ps5Image.getContentAsByteArray());

        final MvcTestResult productImageResult = mockMvcTester.post()
                .uri(ProductController.PATH + "/" + productEntity.getId() + "/images")
                .multipart()
                .file(image)
                .exchange();

        assertThat(productImageResult)
                .hasStatus(HttpStatus.CREATED);


        final ProductImageResponse productImageResponse = objectMapper.readValue(productImageResult.getResponse().getContentAsByteArray(), ProductImageResponse.class);

        final String imageUrl = productImageResponse.getImageUrl();
        blobsToDelete.add(imageUrl.substring(imageUrl.lastIndexOf('/') + 1));

        final UrlResource urlResource = new UrlResource(productImageResponse.getImageUrl());
        assertThat(urlResource.getContentAsByteArray())
                .as("The image in the blob storage service must be the same as the uploaded one")
                .isEqualTo(ps5Image.getContentAsByteArray());
    }

    @Test
    public void testGetProductImagesReturnsEmptyListWhenNoImagesExist() {
        final ProductBrandEntity productBrandEntity = productBrandRepository.save(new ProductBrandEntity(null, "Sony"));
        final ProductCategoryEntity productCategoryEntity = productCategoryRepository.save(new ProductCategoryEntity(null, "consoles", "Consoles", "Consoles"));
        final ProductEntity productEntity = productRepository.save(new ProductEntity(null, productCategoryEntity.getId(), productBrandEntity.getId(), "Playstation 5", "Playstation 5", 1000, 10, "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1", false));

        assertThat(mockMvcTester.get()
                .uri(ProductController.PATH + "/" + productEntity.getId() + "/images"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(ProductImageResponse.class))
                .as("The product must not have any images at this point")
                .isEmpty();
    }

    @Test
    public void testGetImagesOfUnexistingProductReturnsNotFound() {
        assertThat(mockMvcTester.get()
                .uri(ProductController.PATH + "/" + UUID.randomUUID() + "/images"))
                .hasStatus(HttpStatus.NOT_FOUND);
    }


    @Test
    public void testGetImagesOfDeletedProductReturnsNotFound() {
        final ProductBrandEntity productBrandEntity = productBrandRepository.save(new ProductBrandEntity(null, "Sony"));
        final ProductCategoryEntity productCategoryEntity = productCategoryRepository.save(new ProductCategoryEntity(null, "consoles", "Consoles", "Consoles"));
        final ProductEntity productEntity = productRepository.save(new ProductEntity(null, productCategoryEntity.getId(), productBrandEntity.getId(), "Playstation 5", "Playstation 5", 1000, 10, "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1", false));

        assertThat(mockMvcTester.delete()
                .uri(ProductController.PATH + "/" + productEntity.getId()))
                .hasStatus(HttpStatus.NO_CONTENT);

        assertThat(mockMvcTester.get()
                .uri(ProductController.PATH + "/" + productEntity.getId() + "/images"))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testCreateImagesOfDeletedProductReturnsNotFound() throws IOException {
        final ProductBrandEntity productBrandEntity = productBrandRepository.save(new ProductBrandEntity(null, "Sony"));
        final ProductCategoryEntity productCategoryEntity = productCategoryRepository.save(new ProductCategoryEntity(null, "consoles", "Consoles", "Consoles"));
        final ProductEntity productEntity = productRepository.save(new ProductEntity(null, productCategoryEntity.getId(), productBrandEntity.getId(), "Playstation 5", "Playstation 5", 1000, 10, "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1", false));

        assertThat(mockMvcTester.delete()
                .uri(ProductController.PATH + "/" + productEntity.getId()))
                .hasStatus(HttpStatus.NO_CONTENT);

        final Resource ps5Image = new ClassPathResource("ps5.avif");
        final MockMultipartFile image = new MockMultipartFile("image", ps5Image.getFilename(), "image/avif", ps5Image.getContentAsByteArray());

        final MvcTestResult productImageResult = mockMvcTester.post()
                .uri(ProductController.PATH + "/" + productEntity.getId() + "/images")
                .multipart()
                .file(image)
                .exchange();

        assertThat(productImageResult)
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteUnexistingProductReturnsNotFound() {
        assertThat(mockMvcTester.delete()
                .uri(ProductController.PATH + "/" + UUID.randomUUID()))
                .hasStatus(HttpStatus.NOT_FOUND);
    }
}
