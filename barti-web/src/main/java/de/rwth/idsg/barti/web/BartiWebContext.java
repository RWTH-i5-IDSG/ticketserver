package de.rwth.idsg.barti.web;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;

import static de.rwth.idsg.barti.web.PropertiesHolder.CONFIG;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class BartiWebContext {

    private AnnotationConfigWebApplicationContext springContext;

    public BartiWebContext() {
        springContext = new AnnotationConfigWebApplicationContext();
        springContext.scan("de.rwth.idsg.barti.web.config");
    }

    public HandlerCollection getHandlers() throws IOException {
        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(
                new Handler[]{
                        initWebApp()
                });
        return handlerList;
    }

    private WebAppContext initWebApp() throws IOException {
        WebAppContext ctx = new WebAppContext();
        ctx.setContextPath(CONFIG.getContextPath());
        ctx.setResourceBase(new ClassPathResource("webapp").getURI().toString());

        // Disable directory listings if no index.html is found.
        ctx.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        ServletHolder web = new ServletHolder("spring-dispatcher", new DispatcherServlet(springContext));

        ctx.addEventListener(new ContextLoaderListener(springContext));
        ctx.addServlet(web, CONFIG.getSpringMapping());

        return ctx;
    }
}
