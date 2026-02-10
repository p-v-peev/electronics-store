package com.example.pvpeev.electronics_store.product.controller;

import com.example.pvpeev.electronics_store.product.dto.ProductBrandRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductBrandResponse;
import com.example.pvpeev.electronics_store.product.repository.ProductBrandRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductBrandControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductBrandRepository productBrandRepository;

    @AfterEach
    public void afterEachTest() {
        productBrandRepository.deleteAll();
    }

    @Test
    public void testCreateBrand() {

        final ProductBrandRequest productBrandRequest = new ProductBrandRequest("Sony");

        final MvcTestResult response = mockMvcTester.post()
                .uri(ProductBrandController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(productBrandRequest))
                .exchange();

        assertThat(response)
                .hasStatus(HttpStatus.CREATED)
                .containsHeader(HttpHeaders.LOCATION);

        final ProductBrandResponse brandResponse = objectMapper.readValue(response.getResponse().getContentAsByteArray(), ProductBrandResponse.class);

        assertThat(brandResponse.getId()).isNotNull();
        assertThat(brandResponse.getName())
                .as("The name must match exactly the name in the request")
                .isEqualTo(productBrandRequest.getName());
    }

    @Test
    public void testGetAll() {
        final ProductBrandRequest productBrandRequest = new ProductBrandRequest("Sony");

        final MvcTestResult postResponse = mockMvcTester.post()
                .uri(ProductBrandController.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(productBrandRequest))
                .exchange();

        assertThat(postResponse)
                .hasStatus(HttpStatus.CREATED)
                .containsHeader(HttpHeaders.LOCATION);

        final MvcTestResult getResponse = mockMvcTester.get()
                .uri(ProductBrandController.PATH)
                .exchange();

        assertThat(getResponse)
                .hasStatusOk()
                .bodyJson()
                .convertTo(list(ProductBrandResponse.class))
                .as("The list must have only one element")
                .singleElement()
                .satisfies(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getName()).isEqualTo(productBrandRequest.getName());
                });
    }
}
