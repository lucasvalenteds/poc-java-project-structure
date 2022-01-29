package com.example;

import com.example.shared.ServerConfiguration;
import org.eclipse.jetty.server.Server;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

    public static void main(String[] args) throws Exception {
        final var context = new AnnotationConfigApplicationContext(ServerConfiguration.class);
        final var server = context.getBean(Server.class);
        server.start();
    }
}
