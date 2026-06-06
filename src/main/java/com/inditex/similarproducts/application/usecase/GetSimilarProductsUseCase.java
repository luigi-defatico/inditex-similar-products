package com.inditex.similarproducts.application.usecase;

import com.inditex.similarproducts.domain.model.ProductDetail;
import com.inditex.similarproducts.domain.port.ProductDetailPort;
import com.inditex.similarproducts.domain.port.SimilarProductIdsPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Service
public class GetSimilarProductsUseCase {

    private final SimilarProductIdsPort similarProductIdsPort;
    private final ProductDetailPort productDetailPort;

    public GetSimilarProductsUseCase(
            SimilarProductIdsPort similarProductIdsPort,
            ProductDetailPort productDetailPort) {
        this.similarProductIdsPort = similarProductIdsPort;
        this.productDetailPort = productDetailPort;
    }

    public List<ProductDetail> execute(String productId) {
        List<String> similarIds = similarProductIdsPort.getSimilarIds(productId);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Optional<ProductDetail>>> futures = similarIds.stream()
                    .map(id -> CompletableFuture.supplyAsync(
                            () -> productDetailPort.findById(id), executor))
                    .toList();

            return futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }
    }
}