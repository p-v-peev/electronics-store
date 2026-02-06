package com.example.pvpeev.electronics_store.product.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class ImageControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductBrandRepository productBrandRepository;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @AfterEach
    public void afterEachTest() {
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

        final Resource ps5Image = new ClassPathResource("ps5.avif");
        final MockMultipartFile image = new MockMultipartFile("image", ps5Image.getFilename(), "image/avif", ps5Image.getContentAsByteArray());
        final ProductRequest productRequest = new ProductRequest(brandResponse.getId(), "PlayStation 5", "PlayStation 5", 1000, 10);
        final MockPart request = new MockPart("request", objectMapper.writeValueAsBytes(productRequest));
        request.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        final MvcTestResult productResult = mockMvcTester.post()
                .uri(ProductCategoryController.PATH + "/" + categoryRequest.getPath() + "/products")
                .multipart()
                .file(image)
                .part(request)
                .exchange();
        assertThat(productResult).hasStatus(HttpStatus.CREATED);
    }
}
