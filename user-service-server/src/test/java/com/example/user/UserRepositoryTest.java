package com.example.user;

import com.example.shared.DatabaseConfiguration;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringJUnitConfig(DatabaseConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
class UserRepositoryTest {

    @Container
    static final MySQLContainer<?> CONTAINER =
        new MySQLContainer<>(DockerImageName.parse("mysql:8"))
            .withUrlParam("useSSL", "false");

    @DynamicPropertySource
    static void setApplicationProperties(DynamicPropertyRegistry registry) {
        registry.add("database.url", CONTAINER::getJdbcUrl);
        registry.add("database.user", CONTAINER::getUsername);
        registry.add("database.password", CONTAINER::getPassword);
    }

    private static UserRepository userRepository;

    private static List<UserTable> usersInserted;
    private static UserTable userInserted;

    @BeforeAll
    static void beforeAll(ApplicationContext context) {
        userRepository = new UserRepository(context.getBean(DataSource.class));

        context.getBean(Flyway.class)
            .migrate();

        usersInserted = UserTestBuilder.USER_REQUESTS.stream()
            .map(userRequest -> userRepository.insert(new UserTableInsert(userRequest.name(), userRequest.age())))
            .toList();
    }

    @Test
    @Order(1)
    void testInserting() {
        final var userRequest = UserTestBuilder.USER_REQUEST;

        userInserted = userRepository.insert(new UserTableInsert(userRequest.name(), userRequest.age()));

        assertNotNull(userInserted.id(), "Insert should generate a ID to the user");
        assertEquals(userRequest.name(), userInserted.firstName());
        assertEquals(userRequest.age(), userInserted.age());
    }

    @Test
    @Order(2)
    void testFindingById() {
        final var userFound = userRepository.findById(userInserted.id());

        assertEquals(userInserted, userFound);
    }

    @Test
    @Order(3)
    void testRemoving() {
        assertDoesNotThrow(() -> userRepository.remove(userInserted.id()));
    }

    @Test
    void testNameExists() {
        final var userInserted = userRepository.insert(new UserTableInsert("Mary Jane", 32));

        final var exists = userRepository.existsByName(userInserted.firstName());

        assertTrue(exists);

        userRepository.remove(userInserted.id());
    }

    @Test
    void testNameDoesNotExists() {
        final var name = "Edgar Brooks";

        final var exists = userRepository.existsByName(name);

        assertFalse(exists);
    }

    @Test
    void testCounting() {
        Long usersFound = userRepository.count(new UserResponseFilter());

        assertEquals(usersInserted.size(), usersFound);
    }

    @Test
    void testFindingAllUsers() {
        List<UserTable> usersFound = userRepository.findAll(new UserResponseFilter());

        assertEquals(usersInserted, usersFound);
    }

    @Test
    void testFindingAllUsersByName() {
        final var filter = new UserResponseFilter()
            .name(UserTestBuilder.USER_WITH_SAME_NAME);

        final var usersFound = userRepository.findAll(filter);

        assertThat(usersFound)
            .hasSize(2)
            .extracting(UserTable::firstName)
            .allMatch(name -> name.endsWith(filter.getName()));
    }

    @Test
    void testFindingAllUsersByAge() {
        final var filter = new UserResponseFilter()
            .age(UserTestBuilder.USER_WITH_SAME_AGE);

        final var usersFound = userRepository.findAll(filter);

        assertThat(usersFound)
            .hasSize(2)
            .extracting(UserTable::age)
            .containsOnly(filter.getAge());
    }
}