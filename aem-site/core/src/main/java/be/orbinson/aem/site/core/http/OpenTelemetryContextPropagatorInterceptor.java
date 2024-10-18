package be.orbinson.aem.site.core.http;

import be.orbinson.aem.opentelemetry.services.api.OpenTelemetryFactory;
import io.opentelemetry.context.Context;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = OpenTelemetryContextPropagatorInterceptor.class)
public class OpenTelemetryContextPropagatorInterceptor implements HttpRequestInterceptor {

    @Reference
    private OpenTelemetryFactory openTelemetryFactory;

    @Override
    public void process(HttpRequest request, HttpContext context) {
        openTelemetryFactory.get().getPropagators().getTextMapPropagator()
                .inject(Context.current(), request, (req, key, value) -> request.addHeader(key, value));
    }
}
