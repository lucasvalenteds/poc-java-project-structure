package com.example.shared;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.util.UUID;

class ServiceExceptionHandlersTest {

    private final WebTestClient webTestClient = MockMvcWebTestClient
        .bindToController(new ServiceExceptionController())
        .controllerAdvice(new ServiceExceptionHandlers())
        .build();

    @Test
    void testHandlingExceptionAsInternalServerError() {
        webTestClient.get()
            .uri("/service")
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody().isEmpty();
    }

    @Test
    void testHandlingServiceResourceExceptionAsNotFound() {
        webTestClient.get()
            .uri("/resource/{id}", UUID.randomUUID())
            .exchange()
            .expectStatus().isNotFound()
            .expectBody().isEmpty();
    }

    @Test
    void testHandlingServiceValidationExceptionAsBadRequest() {
        webTestClient.get()
            .uri("/validation")
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.code").isNumber()
            .jsonPath("$.message").exists();
    }
}