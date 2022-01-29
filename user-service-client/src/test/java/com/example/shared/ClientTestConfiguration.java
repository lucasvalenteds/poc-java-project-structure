package com.example.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

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
}
