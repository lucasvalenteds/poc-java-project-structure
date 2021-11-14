package com.example.user;

import com.example.shared.ClientTestConfiguration;
import com.example.shared.DatabaseConfiguration;
import com.example.shared.ServiceClientException;
import com.example.shared.ServiceConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringJUnitWebConfig({ClientTestConfiguration.class, DatabaseConfiguration.class, ServiceConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceClientTest {

    private static UserService userService;
    private static UUID userId;

    @BeforeAll
    public static void beforeAll(ApplicationContext context) throws Exception {
        var httpClient = context.getBean(HttpClient.class);
        var objectMapper = context.getBean(ObjectMapper.class);
        var serverUri = context.getBean(URI.class);
        userService = new UserServiceClient(httpClient, objectMapper, serverUri);

        var server = context.getBean(Server.class);
        server.start();
    }

    @AfterAll
    public static void afterAll(ApplicationContext context) throws Exception {
        var server = context.getBean(Server.class);
        server.stop();
    }

    @Test
    @Order(1)
    void testCreating() {
        var userRequest = new UserRequest("John Smith", 45);

        var userResponse = userService.create(userRequest);
        userId = userResponse.id();

        assertNotNull(userResponse.id());
        assertEquals(userRequest.name(), userResponse.name());
        assertEquals(userRequest.age(), userResponse.age());
    }

    @Test
    @Order(2)
    void testFindingById() {
        var userResponse = userService.findById(userId);

        assertEquals(userId, userResponse.id());
        assertEquals("John Smith", userResponse.name());
        assertEquals(45, userResponse.age());
    }

    @Test
    @Order(3)
    void testFindingAll() {
        var filter = new UserResponseFilter();

        var response = userService.findAll(filter);

        assertEquals(1L, response.total());
        assertTrue(response.items().contains(new UserResponse(userId, "John Smith", 45)));
    }

    @Test
    @Order(4)
    void testDeleting() {
        userService.remove(userId);

        assertThrows(ServiceClientException.class, () -> userService.findById(userId));
    }
}