package com.example.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.net.URI;
import java.net.http.HttpClient;

@Configuration
@PropertySource("classpath:client.properties")
public class ClientTestConfiguration {

    @Bean
    HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    URI serverUri(Environment environment) {
        return URI.create(environment.getRequiredProperty("server.uri", String.class));
    }

    @Bean
    Server jetty(URI serverUri) {
        var context = new AnnotationConfigWebApplicationContext();
        context.scan("com.example");

        var contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath("/");

        var dispatcherServlet = new DispatcherServlet(context);
        var servletHolder = new ServletHolder("mvc-dispatcher", dispatcherServlet);
        contextHandler.addServlet(servletHolder, "/");
        contextHandler.addEventListener(new ContextLoaderListener(context));

        var server = new Server(serverUri.getPort());
        server.setHandler(contextHandler);

        return server;
    }
}
