package com.inditex.similarproducts.infrastructure.adapter.http;

import com.inditex.similarproducts.domain.exception.ProductNotFoundException;
import com.inditex.similarproducts.domain.model.ProductDetail;
import com.inditex.similarproducts.domain.port.ProductDetailPort;
import com.inditex.similarproducts.domain.port.SimilarProductIdsPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
public class ProductApiAdapter implements SimilarProductIdsPort, ProductDetailPort {

    private final WebClient webClient;
    private final Duration timeout;

    public ProductApiAdapter(
            WebClient productApiClient,
            @Value("${product.api.timeout-ms}") long timeoutMs) {
        this.webClient = productApiClient;
        this.timeout = Duration.ofMillis(timeoutMs);
    }

    @Override
    public List<String> getSimilarIds(String productId) {
        return webClient.get()
                .uri("/product/{id}/similarids", productId)
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        response -> Mono.error(new ProductNotFoundException(productId)))
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.createException()
                                .map(ex -> new RuntimeException(
                                        "Failed to fetch similar IDs for product " + productId, ex)))
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .timeout(timeout)
                .block();
    }

    @Override
    public Optional<ProductDetail> findById(String productId) {
        return webClient.get()
                .uri("/product/{id}", productId)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.createException()
                                .map(ex -> new RuntimeException("Product not found: " + productId, ex)))
                .bodyToMono(ProductDetailResponse.class)
                .timeout(timeout)
                .map(ProductDetailResponse::toDomain)
                .map(Optional::of)
                .onErrorReturn(Optional.empty())
                .block();
    }
}