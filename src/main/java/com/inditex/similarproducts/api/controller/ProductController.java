package com.inditex.similarproducts.api.controller;

import com.inditex.similarproducts.application.usecase.GetSimilarProductsUseCase;
import com.inditex.similarproducts.domain.model.ProductDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;

    public ProductController(GetSimilarProductsUseCase getSimilarProductsUseCase) {
        this.getSimilarProductsUseCase = getSimilarProductsUseCase;
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductDetail>> getSimilarProducts(
            @PathVariable String productId) {
        List<ProductDetail> similarProducts = getSimilarProductsUseCase.execute(productId);
        return ResponseEntity.ok(similarProducts);
    }
}