package be.orbinson.spring.productservice.service;

import be.orbinson.spring.productservice.model.Product;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    WebClient webClient;

    public ProductService(WebClient.Builder webClientBuilder) {
        // Use WebClient from provided by be.orbinson.spring.productservice.configuration.WebClientConfig
        this.webClient = webClientBuilder
                .filter(ExchangeFilterFunctions.basicAuthentication("admin", "admin"))
                .build();
    }

    @WithSpan
    public Product getProduct(String id) throws Exception {
        // Generate service (subspan) error
        if (id.equals("caught-error")) {
            LOGGER.warn("Product with id 0 should not be used");
            throw new Exception("Error while fetching products");
        }

        String title = "Product Title";
        // Do a call to another http service
        if (id.equals("backend") || id.equals("backend-error")) {
            title = "Product Title from backend";
            queryBackend();
        }

        LOGGER.info("Fetch product with id '{}'", id);
        return new Product(id, title);
    }

    private void queryBackend() {
        String response = webClient.get()
                .uri("http://127.0.0.1:4502/example/servlet")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        LOGGER.info("Got response: {}", response);
    }
}
