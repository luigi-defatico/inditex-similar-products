package com.inditex.similarproducts.infrastructure.adapter.http;

import com.inditex.similarproducts.domain.model.ProductDetail;

public record ProductDetailResponse(
        String id,
        String name,
        double price,
        boolean availability
) {
    public ProductDetail toDomain() {
        return new ProductDetail(id, name, price, availability);
    }
}