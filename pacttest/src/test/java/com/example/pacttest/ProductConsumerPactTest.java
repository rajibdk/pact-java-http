package com.example.pacttest;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.pacttest.model.Product;
import com.example.pacttest.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
public class ProductConsumerPactTest {
    @Pact(consumer = "ConsumerApplication", provider = "ProductService")
    public RequestResponsePact getAllProducts(PactDslWithProvider provider) {
        return provider.given("products exsists")
                .uponReceiving("get all products")
                .method("GET")
                .path("/products")
                .willRespondWith()
                .status(200)
                .headers(headers())
                .body(LambdaDsl.newJsonArrayMinLike(2, array -> array.object(object -> {
                        object.stringType("id", "09");
                        object.stringType("type", "CREDIT_CARD");
                        object.stringType("name", "Gem Visa");
                    })).build()
                ).toPact();
    }

    @Pact(consumer = "ConsumerApplication", provider = "ProductService")
    public RequestResponsePact getOneProduct(PactDslWithProvider provider) {
        return provider.given("Product with ID 10 exists")
                .uponReceiving("Get product with ID 10")
                .method("GET")
                .path("/products/10")
                .willRespondWith()
                .status(200)
                .headers(headers())
                .body(LambdaDsl.newJsonBody(object -> {
                    object.stringType("id", "10");
                    object.stringType("type", "CREDIT_CARD");
                    object.stringType("name", "28 Degrees");
                }).build()).toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getAllProducts")
    void getAllProducts_whenProductExists(MockServer mockServer) {
        Product product = new Product();
        product.setId("09");
        product.setType("CREDIT_CARD");
        product.setName("Gem Visa");
        List<Product> expected = List.of(product, product);

        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        List<Product> actual = new ProductService(restTemplate).getAllProducts();
        assertEquals(expected, actual);
    }

    @Test
    @PactTestFor(pactMethod = "getOneProduct")
    void getOneProduct_whenProductWithId10Exists(MockServer mockServer) {
        Product expected = new Product();
        expected.setId("10");
        expected.setType("CREDIT_CARD");
        expected.setName("28 Degrees");

        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        Product product = new ProductService(restTemplate).getProduct("10");

        assertEquals(expected, product);
    }

    private Map<String, String> headers() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }
}
