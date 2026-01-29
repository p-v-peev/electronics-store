package com.example.pvpeev.electronics_store.product.controller;


import com.example.pvpeev.electronics_store.NumericConstants;
import com.example.pvpeev.electronics_store.product.dto.ProductCategoryRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductCategoryResponse;
import com.example.pvpeev.electronics_store.product.dto.ProductRequest;
import com.example.pvpeev.electronics_store.product.dto.ProductResponse;
import com.example.pvpeev.electronics_store.product.service.ProductCategoryService;
import com.example.pvpeev.electronics_store.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import static com.example.pvpeev.electronics_store.product.controller.ProductCategoryController.PATH;

@RestController
@RequestMapping(PATH)
@RequiredArgsConstructor
public class ProductCategoryController {

    public static final String PATH = "/api/v1/categories";

    private final ProductCategoryService productCategoryService;

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductCategoryResponse>> getAll() {
        return ResponseEntity.ok(productCategoryService.findAll());
    }

    @PostMapping
    public ResponseEntity<ProductCategoryResponse> create(@RequestBody ProductCategoryRequest request, UriComponentsBuilder ucb) {
        final ProductCategoryResponse response = productCategoryService.create(request);
        return ResponseEntity.created(ucb.path(PATH).build().toUri()).body(response);
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(@PathVariable("id") Integer id, @PageableDefault Pageable pageable) {
        if (pageable.getPageSize() > NumericConstants.MAX_PAGE_REQUEST.getValue()) {
            return ResponseEntity.badRequest().build();
        }
        final Page<ProductResponse> page = productService.getAll(id, pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/{id}/products")
    public ResponseEntity<ProductResponse> create(@ModelAttribute ProductRequest request, @PathVariable("id") UUID id, UriComponentsBuilder ucb) {
        final String s = productService.create(request, id);
        return ResponseEntity.created(ucb.path(PATH).pathSegment("{id}", "products").build(s)).build();
    }

    // TODO handle delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Integer id) {
        productCategoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
