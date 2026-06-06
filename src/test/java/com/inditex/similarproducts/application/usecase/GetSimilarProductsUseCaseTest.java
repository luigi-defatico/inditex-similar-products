package com.inditex.similarproducts.application.usecase;

import com.inditex.similarproducts.domain.model.ProductDetail;
import com.inditex.similarproducts.domain.port.ProductDetailPort;
import com.inditex.similarproducts.domain.port.SimilarProductIdsPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSimilarProductsUseCaseTest {

    @Mock
    private SimilarProductIdsPort similarProductIdsPort;

    @Mock
    private ProductDetailPort productDetailPort;

    @InjectMocks
    private GetSimilarProductsUseCase useCase;

    @Test
    void returnsProductDetailsForAllSimilarIds() {
        var shirt = new ProductDetail("1", "Shirt", 9.99, true);
        var dress = new ProductDetail("2", "Dress", 19.99, true);

        when(similarProductIdsPort.getSimilarIds("1")).thenReturn(List.of("1", "2"));
        when(productDetailPort.findById("1")).thenReturn(Optional.of(shirt));
        when(productDetailPort.findById("2")).thenReturn(Optional.of(dress));

        List<ProductDetail> result = useCase.execute("1");

        assertThat(result).hasSize(2).containsExactlyInAnyOrder(shirt, dress);
    }

    @Test
    void skipsProductsWhenDetailIsUnavailable() {
        var shirt = new ProductDetail("1", "Shirt", 9.99, true);

        when(similarProductIdsPort.getSimilarIds("1")).thenReturn(List.of("1", "2"));
        when(productDetailPort.findById("1")).thenReturn(Optional.of(shirt));
        when(productDetailPort.findById("2")).thenReturn(Optional.empty());

        List<ProductDetail> result = useCase.execute("1");

        assertThat(result).hasSize(1).containsExactly(shirt);
    }

    @Test
    void returnsEmptyListWhenNoSimilarIdsExist() {
        when(similarProductIdsPort.getSimilarIds("1")).thenReturn(List.of());

        List<ProductDetail> result = useCase.execute("1");

        assertThat(result).isEmpty();
    }

    @Test
    void returnsEmptyListWhenAllProductDetailsAreUnavailable() {
        when(similarProductIdsPort.getSimilarIds("1")).thenReturn(List.of("2", "3"));
        when(productDetailPort.findById("2")).thenReturn(Optional.empty());
        when(productDetailPort.findById("3")).thenReturn(Optional.empty());

        List<ProductDetail> result = useCase.execute("1");

        assertThat(result).isEmpty();
    }
}