package be.orbinson.spring.productservice.service;

import be.orbinson.spring.productservice.model.Product;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

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
    public Product getProduct(@SpanAttribute String id) throws Exception {
        // Generate service (subspan) error
        if (id.equals("product-service-error")) {
            LOGGER.warn("Product should not be used");
            throw new Exception("Error while fetching products");
        }

        String title = WordUtils.capitalizeFully(id.replace("-", " "));
        // Do a call to another http service
        if (id.equals("backend") || id.equals("backend-error")) {
            title = queryBackend();
        }

        LOGGER.info("Fetch product with id '{}'", id);
        return new Product(id, title);
    }

    private String queryBackend() {
        return webClient.get()
                .uri("http://127.0.0.1:4502/apps/aem-site/endpoints/example/api")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
