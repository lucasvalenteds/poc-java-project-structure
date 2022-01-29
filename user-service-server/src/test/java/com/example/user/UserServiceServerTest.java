package com.example.user;

import com.example.shared.ServiceResourceException;
import com.example.shared.ServiceValidationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceServerTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserMapper userMapper = new UserMapper();
    private final UserService userService = new UserServiceServer(userRepository, userMapper);

    @Test
    void testCreating() {
        final var userRequest = UserTestBuilder.USER_REQUEST;
        final var userResponse = UserTestBuilder.USER_RESPONSE;

        Mockito.when(userRepository.existsByName(userRequest.name()))
            .thenReturn(false);
        Mockito.when(userRepository.insert(UserTestBuilder.USER_TABLE_INSERT))
            .thenReturn(UserTestBuilder.USER_TABLE);

        final var userCreated = userService.create(userRequest);

        assertEquals(userResponse, userCreated);
        Mockito.verify(userRepository, Mockito.times(1))
            .existsByName(userRequest.name());
        Mockito.verify(userRepository, Mockito.times(1))
            .insert(UserTestBuilder.USER_TABLE_INSERT);
    }

    @Test
    void testCreatingWithSameNameThrows() {
        final var userRequest = UserTestBuilder.USER_REQUEST;

        Mockito.when(userRepository.existsByName(userRequest.name()))
            .thenReturn(true);

        final var exception = assertThrows(
            ServiceValidationException.class,
            () -> userService.create(userRequest)
        );

        assertEquals(UserServiceErrors.UserAlreadyExist, exception.getError());
        Mockito.verify(userRepository, Mockito.times(1))
            .existsByName(userRequest.name());
        Mockito.verify(userRepository, Mockito.times(0))
            .insert(UserTestBuilder.USER_TABLE_INSERT);
    }

    @Test
    void testRemoving() {
        final var userResponse = UserTestBuilder.USER_RESPONSE;

        Mockito.doNothing()
            .when(userRepository).remove(userResponse.id());

        userService.remove(userResponse.id());

        Mockito.verify(userRepository, Mockito.times(1))
            .remove(userResponse.id());
    }

    @Test
    void testRemovingWithUnknownIdThrows() {
        final var userResponse = UserTestBuilder.USER_RESPONSE;
        final var userId = userResponse.id();

        Mockito.doThrow(new IncorrectResultSizeDataAccessException(1, 0))
            .when(userRepository).remove(userId);

        final var exception = assertThrows(
            ServiceResourceException.class,
            () -> userService.remove(userId)
        );

        assertEquals("Resource not found by ID", exception.getMessage());
        assertEquals(Optional.of(userId), exception.getResourceId());
        Mockito.verify(userRepository, Mockito.times(1))
            .remove(userId);
    }

    @Test
    void testFindingById() {
        final var userResponse = UserTestBuilder.USER_RESPONSE;
        final var userTable = UserTestBuilder.USER_TABLE;

        Mockito.when(userRepository.findById(userTable.id()))
            .thenReturn(userTable);

        final var userFound = userService.findById(userTable.id());

        assertEquals(userResponse, userFound);
        Mockito.verify(userRepository, Mockito.times(1))
            .findById(userTable.id());
    }

    @Test
    void testFindingByUnknownIdThrows() {
        final var userResponse = UserTestBuilder.USER_RESPONSE;
        final var userId = userResponse.id();

        Mockito.when(userRepository.findById(userId))
            .thenThrow(new IncorrectResultSizeDataAccessException(1));

        final var exception = assertThrows(
            ServiceResourceException.class,
            () -> userService.findById(userId)
        );

        assertEquals("Resource not found by ID", exception.getMessage());
        assertEquals(Optional.of(userId), exception.getResourceId());
        Mockito.verify(userRepository, Mockito.times(1))
            .findById(userId);
    }

    @Test
    void testFindingAll() {
        final var filter = UserTestBuilder.USER_RESPONSE_FILTER;
        final var users = List.of(UserTestBuilder.USER_REQUESTS);
        final var amountOfUsers = Long.valueOf(users.size());

        Mockito.when(userRepository.findAll(filter))
            .thenReturn(List.of(UserTestBuilder.USER_TABLE));
        Mockito.when(userRepository.count(filter))
            .thenReturn(amountOfUsers);

        final var serviceResponse = userService.findAll(filter);

        assertNotNull(serviceResponse.items());
        assertEquals(amountOfUsers, serviceResponse.total());
        Mockito.verify(userRepository, Mockito.times(1))
            .findAll(filter);
        Mockito.verify(userRepository, Mockito.times(1))
            .count(filter);
    }
}