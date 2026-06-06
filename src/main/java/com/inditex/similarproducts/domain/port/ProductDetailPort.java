package com.inditex.similarproducts.domain.port;

import com.inditex.similarproducts.domain.model.ProductDetail;

import java.util.Optional;

public interface ProductDetailPort {

    /**
     * Returns the detail of a product by its ID.
     * Returns empty if the product does not exist or is unavailable.
     *
     * @param productId the product ID
     * @return product detail, or empty if not found
     */
    Optional<ProductDetail> findById(String productId);
}