package com.inditex.similarproducts.domain.port;

import java.util.List;

public interface SimilarProductIdsPort {

    /**
     * Returns the IDs of products similar to the given product.
     *
     * @param productId the reference product ID
     * @return list of similar product IDs, ordered by similarity
     */
    List<String> getSimilarIds(String productId);
}