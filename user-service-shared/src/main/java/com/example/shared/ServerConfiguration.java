package com.example.shared;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@PropertySource("classpath:application.properties")
public class ServerConfiguration {

    @Bean
    Server server(Environment environment) {
        final var context = new AnnotationConfigWebApplicationContext();
        context.scan("com.example");

        final var contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath("/");

        final var dispatcherServlet = new DispatcherServlet(context);
        final var servletHolder = new ServletHolder("mvc-dispatcher", dispatcherServlet);
        contextHandler.addServlet(servletHolder, "/");
        contextHandler.addEventListener(new ContextLoaderListener(context));

        final var server = new Server(environment.getRequiredProperty("server.port", Integer.class));
        server.setHandler(contextHandler);

        return server;
    }
}
