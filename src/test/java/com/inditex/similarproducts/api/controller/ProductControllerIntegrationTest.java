package com.inditex.similarproducts.api.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("product.api.base-url", wireMock::baseUrl);
    }

    @Value("${local.server.port}")
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void returnsSimilarProductsForValidProductId() {
        wireMock.stubFor(get("/product/1/similarids")
                .willReturn(okJson("[\"2\",\"3\"]")));
        wireMock.stubFor(get("/product/2")
                .willReturn(okJson("{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}")));
        wireMock.stubFor(get("/product/3")
                .willReturn(okJson("{\"id\":\"3\",\"name\":\"Blazer\",\"price\":29.99,\"availability\":false}")));

        webTestClient.get()
                .uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Dress", "Blazer"));
    }

    @Test
    void returns404WhenProductNotFound() {
        wireMock.stubFor(get("/product/99/similarids")
                .willReturn(notFound()));

        webTestClient.get()
                .uri("/product/99/similar")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void skipsProductsWithNotFoundError() {
        wireMock.stubFor(get("/product/4/similarids")
                .willReturn(okJson("[\"5\",\"6\"]")));
        wireMock.stubFor(get("/product/5")
                .willReturn(notFound()));
        wireMock.stubFor(get("/product/6")
                .willReturn(okJson("{\"id\":\"6\",\"name\":\"Coat\",\"price\":89.99,\"availability\":true}")));

        webTestClient.get()
                .uri("/product/4/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body).contains("Coat");
                    assertThat(body).doesNotContain("\"id\":\"5\"");
                });
    }

    @Test
    void skipsProductsWithServerError() {
        wireMock.stubFor(get("/product/4/similarids")
                .willReturn(okJson("[\"5\",\"6\"]")));
        wireMock.stubFor(get("/product/5")
                .willReturn(serverError()));
        wireMock.stubFor(get("/product/6")
                .willReturn(okJson("{\"id\":\"6\",\"name\":\"Coat\",\"price\":89.99,\"availability\":true}")));

        webTestClient.get()
                .uri("/product/4/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body).contains("Coat");
                    assertThat(body).doesNotContain("\"id\":\"5\"");
                });
    }
}