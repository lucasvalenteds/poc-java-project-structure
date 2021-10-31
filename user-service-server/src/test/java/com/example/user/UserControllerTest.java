package com.example.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

class UserControllerTest {

    private final UserService userService = Mockito.mock(UserService.class);

    private final WebTestClient webTestClient = MockMvcWebTestClient
        .bindToController(new UserController(userService))
        .build();

    @Test
    void testCreating() {
        var userRequest = UserTestBuilder.USER_REQUEST;
        var userResponse = UserTestBuilder.USER_RESPONSE;
        Mockito.when(userService.create(userRequest))
            .thenReturn(userResponse);

        webTestClient.post()
            .uri("/users")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(userRequest))
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.id").exists()
            .jsonPath("$.name").isEqualTo(userRequest.name())
            .jsonPath("$.age").isEqualTo(userRequest.age());

        Mockito.verify(userService, Mockito.times(1))
            .create(userRequest);
    }

    @Test
    void testRemoving() {
        var userResponse = UserTestBuilder.USER_RESPONSE;
        Mockito.doNothing()
            .when(userService).remove(userResponse.id());

        webTestClient.delete()
            .uri("/users/{id}", userResponse.id())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isAccepted()
            .expectBody().isEmpty();

        Mockito.verify(userService, Mockito.times(1))
            .remove(userResponse.id());
    }

    @Test
    void testFindingById() {
        var userResponse = UserTestBuilder.USER_RESPONSE;
        Mockito.when(userService.findById(userResponse.id()))
            .thenReturn(userResponse);

        webTestClient.get()
            .uri("/users/{id}", userResponse.id())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(String.valueOf(userResponse.id()))
            .jsonPath("$.name").isEqualTo(userResponse.name())
            .jsonPath("$.age").isEqualTo(userResponse.age());

        Mockito.verify(userService, Mockito.times(1))
            .findById(userResponse.id());
    }

    @Test
    void testFindingAll() {
        var filter = UserTestBuilder.USER_RESPONSE_FILTER;
        var userResponse = UserTestBuilder.USER_RESPONSE;
        var serviceResponse = UserTestBuilder.SERVICE_RESPONSE_USERS;
        Mockito.when(userService.findAll(filter))
            .thenReturn(serviceResponse);

        webTestClient.get()
            .uri("/users")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.items").isArray()
            .jsonPath("$.items[0].id").isEqualTo(String.valueOf(userResponse.id()))
            .jsonPath("$.items[0].name").isEqualTo(userResponse.name())
            .jsonPath("$.items[0].age").isEqualTo(userResponse.age())
            .jsonPath("$.total").isEqualTo(serviceResponse.total());

        Mockito.verify(userService, Mockito.times(1))
            .findAll(filter);
    }
}