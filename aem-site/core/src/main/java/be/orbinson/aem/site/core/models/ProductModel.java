package be.orbinson.aem.site.core.models;

import be.orbinson.aem.opentelemetry.services.api.OpenTelemetryFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.instrumentation.apachehttpclient.v4_3.ApacheHttpClientTelemetry;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;

import java.io.IOException;

@Model(adaptables = SlingHttpServletRequest.class)
public class ProductModel {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private OpenTelemetryFactory openTelemetryFactory;

    public String getName() {
        String productId = request.getParameter("productId");
        if (productId == null) {
            return "No product id given, add productId to URL";
        }
        Product product = getProductById(productId);
        if (product != null) {
            return product.getName();
        }
        return "Undefined product";
    }

    public Product getProductById(String productId) {
        try (CloseableHttpClient client = ApacheHttpClientTelemetry.create(openTelemetryFactory.get()).newHttpClient()) {
            // Create and send a request
            HttpGet httpGet = new HttpGet(String.format("http://localhost:8080/product/%s", productId));

            try (CloseableHttpResponse response = client.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String responseBody = entity != null ? EntityUtils.toString(entity) : null;

                if (statusCode == 200 && responseBody != null) {
                    return objectMapper.readValue(responseBody, Product.class);
                } else if (statusCode == 500) {
                    return new Product("error", "Error in fetching product");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public static class Product {
        private String id;
        private String name;

        public Product() {

        }

        public Product(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }


}
