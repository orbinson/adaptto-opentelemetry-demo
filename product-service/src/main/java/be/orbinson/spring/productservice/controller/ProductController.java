package be.orbinson.spring.productservice.controller;

import be.orbinson.spring.productservice.model.Product;
import be.orbinson.spring.productservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(path = "/product/{id}")
    public ResponseEntity<Product> getProductDetails(
            @PathVariable("id") String id
    ) throws Exception {
        LOGGER.info("Getting Product with id '{}'", id);

        // Generate an uncaught exception in the controller
        if (id.equals("uncaught-error")) {
            throw new Exception("Unexpected error");
        }

        try {
            return new ResponseEntity<>(productService.getProduct(id), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Unable to get product with id '{}'", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
