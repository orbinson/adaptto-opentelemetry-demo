package be.orbinson.spring.productservice.configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.spring.webflux.v5_3.SpringWebfluxTelemetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.WebFilter;

@Configuration
public class WebClientConfig {
    private final SpringWebfluxTelemetry webfluxTelemetry;

    public WebClientConfig(OpenTelemetry openTelemetry) {
        this.webfluxTelemetry = SpringWebfluxTelemetry.builder(openTelemetry).build();
    }

    @Bean
    public WebClient.Builder webClient() {
        WebClient webClient = WebClient.create();
        return webClient.mutate().filters(webfluxTelemetry::addClientTracingFilter);
    }

    @Bean
    public WebFilter webFilter() {
        return webfluxTelemetry.createWebFilterAndRegisterReactorHook();
    }
}
