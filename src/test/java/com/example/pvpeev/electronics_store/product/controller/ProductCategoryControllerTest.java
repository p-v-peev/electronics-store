package com.example.pvpeev.electronics_store.product.controller;

import com.example.pvpeev.electronics_store.blob.BlobStorageService;
import com.example.pvpeev.electronics_store.product.dto.*;
import com.example.pvpeev.electronics_store.product.repository.ProductBrandRepository;
import com.example.pvpeev.electronics_store.product.repository.ProductCategoryRepository;
import com.example.pvpeev.electronics_store.product.repository.ProductRepository;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.pvpeev.electronics_store.TextConstants.PRODUCT_THUMBNAILS_STORAGE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductCategoryControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductBrandRepository productBrandRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private BlobStorageService blobStorageService;

    @AfterEach
    public void afterEachTest() {
        productRepository.deleteAll();
        productCategoryRepository.deleteAll();
        productBrandRepository.deleteAll();
    }

    @Test
    public void testCreateProductThenAssertItAndTheProductThumbnail() throws IOException {

        final ProductBrandRequest brandRequest = new ProductBrandRequest("Sony");
        final MvcTestResult brandResult = mockMvcTester.post()
                .uri(ProductBrandController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(brandRequest))
                .exchange();
        assertThat(brandResult).hasStatus(HttpStatus.CREATED);
        final ProductBrandResponse brandResponse = objectMapper.readValue(brandResult.getResponse().getContentAsByteArray(), ProductBrandResponse.class);

        final ProductCategoryRequest categoryRequest = new ProductCategoryRequest("consoles", "Consoles", "Latest gaming consoles");
        final MvcTestResult categoryResult = mockMvcTester.post()
                .uri(ProductCategoryController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(categoryRequest))
                .exchange();
        assertThat(categoryResult).hasStatus(HttpStatus.CREATED);
        final ProductCategoryResponse categoryResponse = objectMapper.readValue(categoryResult.getResponse().getContentAsByteArray(), ProductCategoryResponse.class);

        final Resource ps5Image = new ClassPathResource("ps5.avif");
        final MockMultipartFile image = new MockMultipartFile("image", ps5Image.getFilename(), "image/avif", ps5Image.getContentAsByteArray());
        final ProductRequest productRequest = new ProductRequest(brandResponse.getId(), "PlayStation 5", "PlayStation 5", 1000, 10);
        final MockPart request = new MockPart("request", objectMapper.writeValueAsBytes(productRequest));
        request.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        final MvcTestResult productCreateResult = mockMvcTester.post()
                .uri(ProductCategoryController.PATH + "/" + categoryRequest.getPath() + "/products")
                .multipart()
                .file(image)
                .part(request)
                .exchange();

        assertThat(productCreateResult).hasStatus(HttpStatus.CREATED);

        final AtomicReference<String> thumbnailReference = new AtomicReference<>();
        try {
            final String productLocation = productCreateResult.getResponse().getHeader(HttpHeaders.LOCATION);
            assertThat(productLocation).isNotNull();

            final MvcTestResult productGetResult = mockMvcTester.get()
                    .uri(productLocation)
                    .exchange();

            assertThat(productGetResult)
                    .hasStatusOk()
                    .bodyJson()
                    .as("The page must contain one item")
                    .extractingPath("$.content[0]")
                    .as("The values must be product response")
                    .convertTo(ProductResponse.class)
                    .satisfies(response -> {
                        assertThat(response.getProductBrandId()).isEqualTo(brandResponse.getId());
                        assertThat(response.getProductCategoryId()).isEqualTo(categoryResponse.getId());
                        assertThat(response.getName()).isEqualTo(productRequest.getName());
                        assertThat(response.getDescription()).isEqualTo(productRequest.getDescription());
                        assertThat(response.getPrice()).isEqualTo(productRequest.getPrice());
                        assertThat(response.getQuantityAvailable()).isEqualTo(productRequest.getQuantityAvailable());
                        assertThat(response.getThumbnailImageUrl()).isNotNull();
                        thumbnailReference.set(response.getThumbnailImageUrl());
                    });

            final UrlResource urlResource = new UrlResource(thumbnailReference.get());
            assertThat(urlResource.getContentAsByteArray())
                    .as("The image in the blob storage service must be the same as the uploaded one")
                    .isEqualTo(ps5Image.getContentAsByteArray());

        } finally {
            final String thumbnailUrl = thumbnailReference.get();
            if (thumbnailUrl != null) {
                blobStorageService.delete(PRODUCT_THUMBNAILS_STORAGE.getValue(), thumbnailUrl.substring(thumbnailUrl.lastIndexOf('/') + 1));
            }
        }
    }
}
