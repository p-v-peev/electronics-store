package com.example.pvpeev.electronics_store.product.controller;

import com.example.pvpeev.electronics_store.blob.BlobStorageService;
import com.example.pvpeev.electronics_store.product.dto.ProductCategoryRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductCategoryResponse;
import com.example.pvpeev.electronics_store.product.dto.ProductRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductResponse;
import com.example.pvpeev.electronics_store.product.entity.ProductBrandEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductCategoryEntity;
import com.example.pvpeev.electronics_store.product.entity.ProductEntity;
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
import java.util.HashSet;
import java.util.Set;

import static com.example.pvpeev.electronics_store.TextConstants.PRODUCT_IMAGES_STORAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

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

    private final Set<String> blobsToDelete = new HashSet<>();

    @AfterEach
    public void afterEachTest() {
        blobsToDelete.forEach(blobId -> blobStorageService.delete(PRODUCT_IMAGES_STORAGE.getValue(), blobId));
        productRepository.deleteAll();
        productCategoryRepository.deleteAll();
        productBrandRepository.deleteAll();
    }

    @Test
    public void testGetAllCategoriesReturnsEmptyList() {
        assertThat(mockMvcTester.get()
                .uri(ProductCategoryController.PATH))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(ProductCategoryResponse.class))
                .isEmpty();
    }

    @Test
    public void testCreateRetrieveCategory() {
        final ProductCategoryRequest categoryRequest = new ProductCategoryRequest("smartphones", "Smartphones", "Smartphones");

        assertThat(mockMvcTester.post()
                .uri(ProductCategoryController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(categoryRequest)))
                .hasStatus(HttpStatus.CREATED)
                .containsHeader(HttpHeaders.LOCATION)
                .bodyJson()
                .convertTo(ProductCategoryResponse.class)
                .satisfies(pcr -> {
                    assertThat(pcr.getId()).isNotNull();
                    assertThat(pcr.getPath()).isEqualTo(categoryRequest.getPath());
                    assertThat(pcr.getName()).isEqualTo(categoryRequest.getName());
                    assertThat(pcr.getDescription()).isEqualTo(categoryRequest.getDescription());
                });

        assertThat(mockMvcTester.get()
                .uri(ProductCategoryController.PATH))
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(ProductCategoryResponse.class))
                .singleElement()
                .satisfies(pcr -> {
                    assertThat(pcr.getId()).isNotNull();
                    assertThat(pcr.getPath()).isEqualTo(categoryRequest.getPath());
                    assertThat(pcr.getName()).isEqualTo(categoryRequest.getName());
                    assertThat(pcr.getDescription()).isEqualTo(categoryRequest.getDescription());
                });
    }

    @Test
    public void testGetProductsUnexistingCategoryReturnsNotFound() {
        assertThat(mockMvcTester.get()
                .uri(ProductCategoryController.PATH + "/unexisting/products"))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetProductsInNewCategoryReturnsEmptyPage() {
        final ProductCategoryEntity categoryEntity = productCategoryRepository.save(new ProductCategoryEntity(null, "smartphones", "Smartphones", "Smartphones"));

        assertThat(mockMvcTester.get()
                .uri(ProductCategoryController.PATH + "/" + categoryEntity.getPath() + "/products"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.content")
                .convertTo(list(ProductResponse.class))
                .isEmpty();
    }

    @Test
    public void testGetCategoryProducts() {
        final ProductBrandEntity brandEntity = productBrandRepository.save(new ProductBrandEntity(null, "Apple"));
        final ProductCategoryEntity categoryEntity = productCategoryRepository.save(new ProductCategoryEntity(null, "smartphones", "Smartphones", "Smartphones"));
        final ProductEntity productEntity = productRepository.save(new ProductEntity(null, categoryEntity.getId(), brandEntity.getId(), "Apple iPhone 17", "Apple iPhone 17", 1000, 10, "http://localhost:9000/product-thumbnails/e619ed60-3cf9-4c5f-9c3d-84a8b1be30a1", false));

        assertThat(mockMvcTester.get()
                .uri(ProductCategoryController.PATH + "/" + categoryEntity.getPath() + "/products"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.content")
                .convertTo(list(ProductResponse.class))
                .singleElement()
                .satisfies(pr -> {
                    assertThat(pr.getId()).isNotNull();
                    assertThat(pr.getProductCategoryId()).isEqualTo(categoryEntity.getId());
                    assertThat(pr.getProductBrandId()).isEqualTo(brandEntity.getId());
                    assertThat(pr.getName()).isEqualTo(productEntity.getName());
                    assertThat(pr.getDescription()).isEqualTo(productEntity.getDescription());
                    assertThat(pr.getPrice()).isEqualTo(productEntity.getPrice());
                    assertThat(pr.getQuantityAvailable()).isEqualTo(productEntity.getQuantityAvailable());
                    assertThat(pr.getThumbnailImageUrl()).isEqualTo(productEntity.getThumbnailImageUrl());
                });
    }

    @Test
    public void testCreateProductThenAssertItAndTheProductThumbnail() throws IOException {

        final ProductBrandEntity brandEntity = productBrandRepository.save(new ProductBrandEntity(null, "Apple"));
        final ProductCategoryEntity categoryEntity = productCategoryRepository.save(new ProductCategoryEntity(null, "smartphones", "Smartphones", "Smartphones"));

        final Resource ps5Image = new ClassPathResource("ps5.avif");
        final MockMultipartFile image = new MockMultipartFile("image", ps5Image.getFilename(), "image/avif", ps5Image.getContentAsByteArray());
        final ProductRequest productRequest = new ProductRequest(brandEntity.getId(), "PlayStation 5", "PlayStation 5", 1000, 10);
        final MockPart request = new MockPart("request", objectMapper.writeValueAsBytes(productRequest));
        request.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        final MvcTestResult productResult = mockMvcTester.post()
                .uri(ProductCategoryController.PATH + "/" + categoryEntity.getPath() + "/products")
                .multipart()
                .file(image)
                .part(request)
                .exchange();

        assertThat(productResult)
                .hasStatus(HttpStatus.CREATED)
                .containsHeader(HttpHeaders.LOCATION);

        final ProductResponse productResponse = objectMapper.readValue(productResult.getResponse().getContentAsByteArray(), ProductResponse.class);
        assertThat(productResponse)
                .satisfies(pr -> {
                    assertThat(pr.getId()).isNotNull();
                    assertThat(pr.getProductCategoryId()).isEqualTo(categoryEntity.getId());
                    assertThat(pr.getProductBrandId()).isEqualTo(brandEntity.getId());
                    assertThat(pr.getName()).isEqualTo(productRequest.getName());
                    assertThat(pr.getDescription()).isEqualTo(productRequest.getDescription());
                    assertThat(pr.getPrice()).isEqualTo(productRequest.getPrice());
                    assertThat(pr.getQuantityAvailable()).isEqualTo(productRequest.getQuantityAvailable());
                    assertThat(pr.getThumbnailImageUrl()).isNotNull();
                });

        final String imageUrl = productResponse.getThumbnailImageUrl();
        blobsToDelete.add(imageUrl.substring(imageUrl.lastIndexOf('/') + 1));

        final UrlResource urlResource = new UrlResource(productResponse.getThumbnailImageUrl());
        assertThat(urlResource.getContentAsByteArray())
                .as("The image in the blob storage service must be the same as the uploaded one")
                .isEqualTo(ps5Image.getContentAsByteArray());
    }

    @Test
    public void testGetProductsDoesntReturnDeletedProducts() throws IOException {

        final ProductBrandEntity brandEntity = productBrandRepository.save(new ProductBrandEntity(null, "Apple"));
        final ProductCategoryEntity categoryEntity = productCategoryRepository.save(new ProductCategoryEntity(null, "smartphones", "Smartphones", "Smartphones"));

        final Resource ps5Image = new ClassPathResource("ps5.avif");
        final MockMultipartFile image = new MockMultipartFile("image", ps5Image.getFilename(), "image/avif", ps5Image.getContentAsByteArray());
        final ProductRequest productRequest = new ProductRequest(brandEntity.getId(), "PlayStation 5", "PlayStation 5", 1000, 10);
        final MockPart request = new MockPart("request", objectMapper.writeValueAsBytes(productRequest));
        request.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        final MvcTestResult productResult = mockMvcTester.post()
                .uri(ProductCategoryController.PATH + "/" + categoryEntity.getPath() + "/products")
                .multipart()
                .file(image)
                .part(request)
                .exchange();

        assertThat(productResult)
                .hasStatus(HttpStatus.CREATED)
                .containsHeader(HttpHeaders.LOCATION);

        final ProductResponse productResponse = objectMapper.readValue(productResult.getResponse().getContentAsByteArray(), ProductResponse.class);
        assertThat(mockMvcTester.delete()
                .uri(ProductController.PATH + "/" + productResponse.getId()))
                .hasStatus(HttpStatus.NO_CONTENT);

        assertThat(mockMvcTester.get()
                .uri(ProductCategoryController.PATH + "/" + categoryEntity.getPath() + "/products"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.content")
                .convertTo(list(ProductResponse.class))
                .isEmpty();
    }
}
