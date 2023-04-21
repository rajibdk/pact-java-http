package com.example.pacttest;

import com.example.pacttest.model.Product;
import com.example.pacttest.service.ProductService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductServiceTest {
    private WireMockServer wireMockServer;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        //((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("wiremock").setLevel(Level.OFF);
        //((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("WireMock").setLevel(Level.OFF);

        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();

        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(wireMockServer.baseUrl()).build();
        productService = new ProductService(restTemplate);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.start();
    }

    @Test
    void getAllProducts() {
        wireMockServer.stubFor(get(urlPathEqualTo("/products"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[" +
                                "{\"id\":\"9\",\"type\":\"CREDIT_CARD\",\"name\":\"GEM Visa\",\"version\":\"v2\"},"+
                                "{\"id\":\"10\",\"type\":\"CREDIT_CARD\",\"name\":\"28 Degrees\",\"version\":\"v1\"}"+
                                "]")));

        List<Product> expected = Arrays.asList(new Product("9", "CREDIT_CARD", "GEM Visa", "v2"),
                new Product("10", "CREDIT_CARD", "28 Degrees", "v1"));

        List<Product> products = productService.getAllProducts();
        assertEquals(expected, products);
    }

    @Test
    void getAProducts() {
        wireMockServer.stubFor(get(urlPathEqualTo("/products/50"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"50\",\"type\":\"CREDIT_CARD\",\"name\":\"28 Degrees\",\"version\":\"v1\"}")));

        Product expected = new Product("50", "CREDIT_CARD", "28 Degrees", "v1");

        Product product = productService.getProduct("50");
        assertEquals(expected, product);
    }
}
