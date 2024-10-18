package be.orbinson.aem.site.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;

@Component
@SlingServletResourceTypes(
        resourceTypes = "aem-site/example/api"
)
public class ExampleApiServlet extends SlingSafeMethodsServlet implements Servlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.getWriter().write("Product title coming from AEM");
    }
}
